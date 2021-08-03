package com.besza;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.context.ThreadContext;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ElviraScraper {

    private static final Logger log = Logger.getLogger(ElviraScraper.class.getName());

    private static final HttpClient client = HttpClient.newHttpClient();

    @Inject
    DbService dbService;

    @Inject
    ThreadContext threadContext;

    @Inject
    ManagedExecutor executor;

    @Scheduled(cron = "{app.scraper.cron}")
    void scheduledScrape() {
        scrape("Esztergom", "BUDAPEST*");
        scrape("Hatvan", "BUDAPEST*");
        scrape("Nyíregyháza", "BUDAPEST*");
        scrape("Székesfehérvár", "BUDAPEST*");
        scrape("Szob", "BUDAPEST*");
        scrape("Szolnok", "BUDAPEST*");
        scrape("BUDAPEST*", "Esztergom");
        scrape("BUDAPEST*", "Hatvan");
        scrape("BUDAPEST*", "Nyíregyháza");
        scrape("BUDAPEST*", "Székesfehérvár");
        scrape("BUDAPEST*", "Szob");
        scrape("Budapest-Keleti", "Szolnok");
    }

    private void scrape(String origin, String destination) {
        // TODO: we dont need context capture anymore I guess.. or do we?
        threadContext.withContextCapture(client.sendAsync(buildRequest(origin, destination), HttpResponse.BodyHandlers.ofString(StandardCharsets.ISO_8859_1)))
                .thenApplyAsync(response -> parse(response.body()), executor)
                .exceptionally(ex -> {
                    log.info("Shit is fucked up " + ex.getMessage());
                    return Collections.emptyList();
                })
                .thenAcceptAsync(entities -> dbService.save(entities), executor);
    }

    private HttpRequest buildRequest(String origin, String destination) {
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

        return HttpRequest.newBuilder()
                .uri(URI.create(elvira.toString()))
                .timeout(Duration.ofSeconds(30L))
                // masking the default UA
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:84.0) Gecko/20100101 Firefox/84.0")
                .GET()
                .build();
    }

    private List<TimetableDTO> parse(final String html) {
        return Jsoup.parse(html)
                .select("div[id^=info] > table > tbody")
                .stream()
                .map(tBody -> Stream.of(
                        foo(tBody.select("tr.sh00").first().select("td")),
                        foo(tBody.select("tr.sh01 > td")))
                        .flatMap(List::stream)
                        .toArray(String[]::new))
                .map(TimetableDTO::new)
                .collect(Collectors.toList());
    }

    // Extracts the first 3 values from the html table row
    private List<String> foo(final Elements elements) {
        return elements.stream()
                .map(elem -> elem.hasText() ? elem.text() : null)
                .limit(3)
                .collect(Collectors.toList());
    }
}
