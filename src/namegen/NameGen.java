package namegen;

import namegen.Log.*;
import java.io.*;
import java.nio.charset.*;

public class NameGen{
    private static boolean exit = false;
    private static File file = new File("name.txt");

    public static synchronized void main(String[] args){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(!exit){
            try{
                System.out.print("Generate? (y/n): ");
                String input = reader.readLine();
                char option = input.charAt(0);

                if(input.length() == 1){
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
                }else{
                    Log.log(LogLevel.warn, "Invalid answer! The only options are 'y' or 'n'.");
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
        String name = reader.readLine();

        StringBuilder result = new StringBuilder();
        int last, num = 0;

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

			if(name.charAt(i) != ' '){
                result.append(name.charAt(i) + Character.toString(seedC));
			}else{
				result.append(' ');
			}
        }

        int iterations = 3, freq = iterations * 2;
        for(int iteration = 0; iteration < iterations; iteration++){
            name = result.toString();
            result.setLength(0);

            for(int i = 0; i < name.length(); i++){
                String c = ((Character)name.charAt(i)).toString();
                result.append(c);

                if((i + iteration % (freq + 1)) == freq){
                    String a = Character.toString(8206) + c + Character.toString(8207);
                    a = Character.toString(8238) + a + Character.toString(8234);

                    result.append(a);
                }
            }
        }

        try(
            FileOutputStream output = new FileOutputStream(file, false);
            OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_16);
        ){
            writer.write(name);
            writer.flush();
        }finally{
            Log.log(LogLevel.info, "Generated name is in: @!", file.getAbsolutePath());
        }
    }
}
