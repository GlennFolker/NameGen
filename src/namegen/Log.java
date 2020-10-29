package namegen;

public class Log{
    public static void log(LogLevel level, String text, Object... args){
        String result = LogLevel.code(level) + " " + parseString(text, args);

        System.out.println(result);
    }

    public static String parseString(String text, Object... args){
        if(text == null){
            if(args.length > 0 && args[0] != null){
                text = "@";
            }else{
                text = "";
            }
        }

        String[] letters = text.split("");
        StringBuilder res = new StringBuilder();

        int index = 0;
        for(String l : letters){
            if(l.equals("@")){
                Object arg = args[index++];

                if(arg != null){
                    res.append(arg.toString());
                }else{
                    throw new IllegalArgumentException("Argument pointer '@' count is greater than argument count!");
                }
            }else{
                res.append(l);
            }
        }

        return res.toString();
    }

    public enum LogLevel{
        info,
        warn,
        error;

        public static String code(LogLevel level){
            switch(level){
                case info: return "[I]";
                case warn: return "[W]";
                case error: return "[E]";
                default: throw new IllegalArgumentException("Unknown log level!");
            }
        }
    }
}
