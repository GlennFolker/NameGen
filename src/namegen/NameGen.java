package namegen;

import namegen.Log.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;

public class NameGen{
    private static boolean exit = false;

    public static synchronized void main(String[] args){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(!exit){
            try{
                System.out.print("Generate? (y/n): ");
                String input = reader.readLine();

                if(input.length() > 0){
                    char option = input.charAt(0);

                    switch(option){
                        case 'y':
                            name(reader);
                            break;
    
                        case 'n':
                            exit = true;
                            break;

                        default:
                            Log.log(LogLevel.warn, "Invalid answer! The only options are 'y' or 'n'.");
                    }
                }
            }catch(Exception e){
                Log.log(LogLevel.error, null, e);

                throw new RuntimeException(e);
            }
        }

        int code = 1;
        Log.log(LogLevel.info, "Program exited with code @", code);

        System.exit(code);
    }

    public static synchronized void name(BufferedReader reader) throws IOException{
        System.out.print("Enter the target name: ");
        String name = reader.readLine().replace("\\n", Character.toString('\n'));

        StringBuilder result = new StringBuilder();
        int last, num = 0;

        for(int iteration = 0; iteration < 3; iteration++){
            for(int i = 0; i < name.length(); i++){
                last = num;

                int a = name.codePointAt((i - 1 + name.length()) % name.length());
                int b = name.codePointAt((i + 1 + name.length()) % name.length());
                int n = name.codePointAt(i);

                num = (n << a >> b) ^ (n >> a << b);

                for(int h = 0; h < 2; h++){
                    int f = name.codePointAt(Math.abs((num + name.length()) % name.length()));

                    if(num <= last){
                        num ^= f;
                    }else{
                        num >>= f;
                    }
                }

                int seed = 768 + (num % 111);
			    int seedB = 710 + (num % 19);
                int seedC = num <= last ? seed : seedB;

                char c = name.charAt(i);

                result.append(Character.toString(c));
                if(!Character.isWhitespace(c)){
                    result.append(Character.toString(seedC));
                }
            }

            name = result.toString();
            result.setLength(0);
        }

        int iterations = 3, freq = iterations * 2;
        for(int iteration = 0; iteration < iterations; iteration++){
            for(int i = 0; i < name.length(); i++){
                char c = name.charAt(i);
                result.append(Character.toString(c));

                if((i + iteration % (freq + 1)) == freq){
                    String a = Character.toString(8206) + Character.toString(c) + Character.toString(8207);
                    a = Character.toString(8238) + a + Character.toString(8234);

                    result.append(a);
                }
            }

            name = result.toString();
            result.setLength(0);
        }

        boolean success = setClipboardString(name);
        if(success){
            Log.log(LogLevel.info, "Generated name has been copied to clipboard!");
        }else{
            Log.log(LogLevel.warn, "Couldn't copy the generated name to clipboard!");
        }
    }

    public static synchronized boolean setClipboardString(String contents){
        try{
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection data = new StringSelection(contents);

            clipboard.setContents(data, data);

            return true;
        }catch(Exception e){
            Log.log(LogLevel.error, null, e);

            return false;
        }
    }
}
