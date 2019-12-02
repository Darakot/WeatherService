package com.example.WeatherApplication.Controllers;

import com.example.WeatherApplication.Parser.ParserWeather;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * При запуске первый раз контроллер смотрит есть ли файл с инфой о прошлом запуске.
 * Если есть файл и в нем есть инфа, то запускаемся с сохранёнными данными, если нет то с инфой по умолчанию.
 * При каждом POST запросе пишем в файл последний запрашиваемый город и сервис, если файла не существует,
 * то сначала создаем его.
 */

@Controller
public class Ctrl {
    private ParserWeather parserWeather = new ParserWeather();


    @GetMapping("/")
    public String main(Map<String,Object> model) throws IOException {
        File saveList = new File("saveList.txt");

        if (!saveList.exists()) {
            List<String> info = parserWeather.yandex("yekaterinburg");
            model.put("weather", info);
            model.put("cityText","Екатеринбург");
            model.put("serviceText","Yandex");
            return "main";
        }else{
            FileReader saveListReader = new FileReader(saveList);
            String sSaveList = "";
            int symb;
            while ((symb=saveListReader.read())!=-1){
                sSaveList+=(char)symb;
            }
            saveListReader.close();
            String[] arrSaveList = sSaveList.split("/");

            if(arrSaveList[1].equals("1")){
                List<String> yandex = parserWeather.yandex(arrSaveList[0]);
                model.put("weather",yandex);
                model.put("cityText",arrSaveList[0]);
                model.put("serviceText","Yandex");
            }else if (arrSaveList[1].equals("2")){
                List<String> foreca = parserWeather.foreca(arrSaveList[0]);
                model.put("weather",foreca);
                model.put("cityText",arrSaveList[0]);
                model.put("serviceText","Foreca");
            }

            return "main";
        }
    }

    @PostMapping("/")
    public String getWeather(@RequestParam String city, @RequestParam String service, Map<String, Object> model) throws IOException {
            File saveList = new File("saveList.txt");

            if (!saveList.exists()) {
                saveList.createNewFile();
            }
            FileWriter saveListWriter = new FileWriter(saveList);
                saveListWriter.write(city + "/" + service);
                saveListWriter.flush();
                saveListWriter.close();

        if(service.equals("1")){
            List<String> yandex = parserWeather.yandex(city);
            model.put("weather",yandex);
            model.put("cityText",city);
            model.put("serviceText","Yandex");
        }else if (service.equals("2")){
            List<String> foreca = parserWeather.foreca(city);
            model.put("weather",foreca);
            model.put("cityText",city);
            model.put("serviceText","Foreca");
        }

        return "main";
    }

}
