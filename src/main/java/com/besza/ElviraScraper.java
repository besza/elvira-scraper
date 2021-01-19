package com.besza;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ElviraScraper {

    private static final Logger LOG = Logger.getLogger(ElviraScraper.class.getName());

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    @Inject
    DbService dbService;

    @Scheduled(cron = "{app.scraper.cron}")
    @Retry(retryOn = IOException.class,
            maxDuration = 20,
            delay = 5,
            durationUnit = ChronoUnit.MINUTES,
            delayUnit = ChronoUnit.MINUTES)
    void scheduledScrape() throws IOException, InterruptedException {
        LOG.info("Starting scheduled job at " + Instant.now());
        scrape("Szolnok", "BUDAPEST*");
        scrape("Nyíregyháza", "BUDAPEST*");
        LOG.info("Finished scheduled job at " + Instant.now());
    }


    private void scrape(String origin, String destination) throws InterruptedException, IOException {
        var currentLocalDate = LocalDate.now();
        var formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        var textDate = currentLocalDate.format(formatter);

        var queryParams = Map.of(
                "go", "Timetable",
                "i", origin,
                "e", destination,
                "isz", "0",
                "mikor", "-1",
                "d", textDate,
                "sk", "5"
        );

        var elvira = new StringBuilder("https://elvira.mav-start.hu/elvira.dll/x/uf?");

        // we don't care about the trailing ampersand
        queryParams.forEach((k, v) -> elvira.append("%s=%s&".formatted(k, v)));

        var request = HttpRequest.newBuilder()
                .uri(URI.create(elvira.toString()))
                .timeout(Duration.ofMinutes(1L))
                // masking the default UA
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0")
                .GET()
                .build();

        var response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.ISO_8859_1));

        var result = Jsoup.parse(response.body())
                .select("div[id^=info] > table > tbody")
                        .stream()
                        .map(tBody -> Stream.of(
                                    foo(tBody.select("tr.sh00").first().select("td")),
                                    foo(tBody.select("tr.sh01 > td")))
                                .flatMap(List::stream)
                                .toArray(String[]::new))
                        .map(TimetableDTO::new)
                        .collect(Collectors.toList());

        LOG.debug(result);
        dbService.save(result);
    }

    // Extracts the first 3 values from the html table row
    private List<String> foo(final Elements elements) {
        return elements.stream()
                .map(elem -> elem.hasText() ? elem.text() : null)
                .limit(3)
                .collect(Collectors.toList());
    }
}
