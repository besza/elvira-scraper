package com.besza;

import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.jboss.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class ElviraScraper {

    private static final Logger LOG = Logger.getLogger(ElviraScraper.class.getName());

    @Inject
    DbService dbService;

    @Scheduled(cron = "{app.scraper.cron}")
    @Retry(retryOn = IOException.class,
            maxDuration = 20,
            delay = 5,
            durationUnit = ChronoUnit.MINUTES,
            delayUnit = ChronoUnit.MINUTES)
    void scheduledScrape() throws IOException {
        LOG.info("Starting scheduled job at " + Instant.now());
        scrape("Szolnok", "BUDAPEST*");
        scrape("Nyíregyháza", "BUDAPEST*");
        LOG.info("Finished scheduled job at " + Instant.now());
    }


    private void scrape(String origin, String destination) throws IOException {
        var currentLocalDate = LocalDate.now();
        var formatter = DateTimeFormatter.ofPattern("yy.MM.dd");
        var textDate = currentLocalDate.format(formatter);

        final var html = Jsoup.connect("https://elvira.mav-start.hu/elvira.dll/x/uf")
                .data("go", "Timetable")
                .data("language", "2")
                .data("_charset_", "UTF-8")
                .data("i", origin)
                .data("e", destination)
                .data("isz", "0")
                .data("mikor", "-1")
                .data("d", textDate)
                // I have no clue about what the following query params do (one of them is `Direct connection`)
                .data("u", "1156")
                .data("sk", "5")
                .get();

        var result =
                html.select("div[id^=info] > table > tbody")
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
