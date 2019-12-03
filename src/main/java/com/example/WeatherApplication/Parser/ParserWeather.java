package com.example.WeatherApplication.Parser;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Парсит с 2 сайтов.
 * При парсинге сайта www.foreca.ru есть интересный момент, когда парсится давление конечно значение отображает в гПа,
 * а не в миллиметрах ртутного столба. Поделив на 1.382, получим значение  в миллиметрах ртутного столба.
 */

@Component
public class ParserWeather {
    private final String forecaSite = "https://www.foreca.ru/Russia/";
    private final String yandexSite = "https://yandex.ru/pogoda/";

    public ParserWeather() {
    }


    //Получаем страницу нужного сервиса
    public static Document getPage(String url, String city) throws IOException {

        Document page = null;
        try {
            page = Jsoup.connect(url + city)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36")
                    .referrer("http://www.google.com")
                    .get();
        } catch (NullPointerException e) {
            e.getMessage();
        } catch (HttpStatusException e) {
            System.out.println(url + " " +e.getStatusCode());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return page;
    }


    //Записываем получившиеся значения в коллекцию.
    private List<String> writeValues (String  wind, String humidity, String pressure, String temperature, String feels) {
        List<String> valuesWeather = new ArrayList<>();
        valuesWeather.add("Ветер: " + wind);
        valuesWeather.add("Влажность: " + humidity);
        valuesWeather.add("Давление: " + pressure);
        valuesWeather.add("Температура: " + temperature);
        valuesWeather.add("Ощущается: " + feels);

        return valuesWeather;
    }

    public List<String> foreca(String city) throws IOException {
        Document pageForeca = getPage(forecaSite , city);
        List<String> valuesForeca = null;

        if (pageForeca!=null){
            Element weatherInfo = pageForeca.select("div.c1").first();
            Elements dataInfo = weatherInfo.select("strong");
            Elements name = weatherInfo.select("br");

            String[] arrPressure = dataInfo.get(3).text().split(" ");
            Double dPressure = Double.valueOf(arrPressure[0]);

            valuesForeca = writeValues(dataInfo.get(1).text(),
                    dataInfo.get(5).text(),
                    String.valueOf(Math.round(dPressure/1.382)),
                    dataInfo.get(0).text(),
                    dataInfo.get(4).text());
        }
        return valuesForeca;
    }

    public List<String> yandex(String city) throws IOException {
        Document pageYandex = getPage(yandexSite , city);
        List<String> valuesYandex = null;

        if (pageYandex!=null){
            Element weatherInfo = pageYandex.select("div.content__row").first();
            Elements tempInfo = weatherInfo.select("span.temp__value");
            Element info = weatherInfo.selectFirst("div.fact__props");
            Elements dataInfo = info.select("div.term__value");

          valuesYandex = writeValues(dataInfo.get(0).text(),
                    dataInfo.get(1).text(),
                    dataInfo.get(2).text(),
                    tempInfo.get(0).text(),
                    tempInfo.get(1).text());
        }
        return valuesYandex;
    }
}
