package namegen;

import namegen.Log.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

public class NameGen{
    private static Random rand = new Random();
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

        for(int i = 0; i < name.length(); i++){
            int min = 0x0300;
            int max = 0x036F;
            int range = max - min;

            rand.setSeed(i);
            int seed = min + rand.nextInt(range + 1);

            char c = name.charAt(i);

            result.append(Character.toString(c));
            if(!Character.isWhitespace(c)){
                result.append(Character.toString(seed));
            }
        }

        int iterations = 3, freq = iterations * 2;

        for(int it = 0; it < iterations; it++){
            name = result.toString();
            result.setLength(0);

            for(int i = 0; i < name.length(); i++){
                char c = name.charAt(i);

                if(i % (freq + 1) == freq){
                    CharSequence a = Character.toString(8206) + Character.toString(c) + Character.toString(8207);
                    result.append(a);
                }else{
                    result.append(Character.toString(c));
                }
            }
        }

        name = result.toString();
        result.setLength(0);

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
