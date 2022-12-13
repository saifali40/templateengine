package in.saifali;

import in.saifali.model.Context;
import in.saifali.model.TestClass;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BluePaleDot {
    public BluePaleDot(String filePath) throws Exception {
        TestClass testClass = new TestClass("saifali",18);
        Context context = new Context("en",testClass);


        System.out.println(getTemplate(filePath, context));
    }


    private String getTemplate(String filePath, Context context) throws Exception {

        Properties properties = getProperties(context.getLanguage());
        Path path = Paths.get(getClass().getClassLoader()
                .getResource(filePath).toURI());
        Stream<String> lines = Files.lines(path);
        return lines.map(line -> {
                    if (Pattern.matches("<%fragment>(.+?)<%fragment>", line)) {
                        try {
                            return getTemplate(line.replaceAll("<%fragment>", "").strip(), context);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return line;
                }
        ).map(line -> {
            final Pattern pattern = Pattern.compile("<%i18>(.+?)<%i18>", Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                String contextKey = matcher.group().replaceAll("<%i18>", "").strip();
                line = line.replaceAll("<%i18>(.+?)<%i18>", properties.getProperty(contextKey));
            }
            return line;
        }).map(line -> {
            try {
                final Pattern pattern = Pattern.compile("<%context>(.+?)<%context>", Pattern.DOTALL);
                final Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String contextKey = matcher.group().replaceAll("<%context>", "").strip();
                    Field field = context.getContext().getClass().getField(contextKey);
                    line = line.replaceAll("<%context>(.+?)<%context>",field.get(context.getContext()) +"");
                    return line;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return line;
        }).collect(Collectors.joining("\n"));
    }

    public Properties getProperties(String lang) {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(String.format("i18/email_%s.properties", lang)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
