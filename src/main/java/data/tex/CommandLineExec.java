package data.tex;

/**
 *@author https://www.infoworld.com/article/2071275/when-runtime-exec---won-t.html?page=2
 * @modified by Edvin Nise
 */
public class CommandLineExec
{
    public static void main(String args[])
    {
        if (args.length < 1)
        {
            System.out.println("USAGE: java CommandLineExec <cmd>");
            System.exit(1);
        }

        try
        {
            String osName = System.getProperty("os.name" );
            String[] cmd = new String[3];
            if( osName.equals( "Windows 10" ) || osName.equals( "Windows 11"))
            {
                cmd[0] = "cmd.exe" ;
                cmd[1] = "/C" ;
                cmd[2] = args[0];
            }
            else if( osName.equals( "Windows 95" ) )
            {
                cmd[0] = "command.com" ;
                cmd[1] = "/C" ;
                cmd[2] = args[0];
            } else if ( osName.contains("mac os")) {
                cmd[0] = "/bin/bash";
                cmd[1] = "-c";
                cmd[2] = args[0];
            }

            Runtime rt = Runtime.getRuntime();
            System.out.println("Execing " + cmd[0] + " " + cmd[1]
                    + " " + cmd[2]);
            Process proc = rt.exec(cmd);
            // any error message?
            StreamGobbler errorGobbler = new
                    StreamGobbler(proc.getErrorStream(), "ERROR");

            // any output?
            StreamGobbler outputGobbler = new
                    StreamGobbler(proc.getInputStream(), "OUTPUT");

            // kick them off
            errorGobbler.start();
            outputGobbler.start();

            // any error???
            int exitVal = proc.waitFor();
            System.out.println("ExitValue: " + exitVal);
        } catch (Throwable t)
        {
            t.printStackTrace();
        }
    }
}
