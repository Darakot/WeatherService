package com.example.WeatherApplication.Controllers;

import com.example.WeatherApplication.Parser.ParserWeather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * При запуске первый раз контроллер смотрит есть ли файл с инфой о прошлом запуске.
 * Если есть файл и в нем есть инфа, то запускаемся с сохранёнными данными, если нет то с инфой по умолчанию.
 * При каждом POST запросе пишем в файл последний запрашиваемый город и сервис, если файла не существует,
 * то сначала создаем его.
 * Для хранения информации между перезапусками используются куки.
 * Время жизни 365 дней.
 */

@Controller
public class ControllerWeather {

    private final ParserWeather parserWeather;

    @Autowired
    public ControllerWeather(ParserWeather parserWeather) {
        this.parserWeather = parserWeather;
    }

    @GetMapping("/")
    public String main(@CookieValue(value = "info", defaultValue = "")String infoCookie, Map<String,Object> model) throws IOException {
        if (infoCookie==null || infoCookie.isEmpty()) {
            List<String> info = parserWeather.yandex("yekaterinburg");
            model.put("weather", info);
            model.put("cityText","Екатеринбург");
            model.put("serviceText","Yandex");
            return "main";
        }else{
            String[] arrSaveList = infoCookie.split("/");

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
    public String getWeather(@RequestParam String city, @RequestParam String service,HttpServletResponse  response, Map<String, Object> model) throws IOException {
            Cookie cookie = new Cookie("info", city + "/" + service);

            cookie.setPath("/");
            cookie.setMaxAge(31536000);
            response.addCookie(cookie);

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
