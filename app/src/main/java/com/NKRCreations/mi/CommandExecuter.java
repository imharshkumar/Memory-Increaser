package com.NKRCreations.mi;

import java.io.DataOutputStream;
import java.io.IOException;

public class CommandExecuter {

    private Process process;
    private DataOutputStream writer;
    private boolean isRooted = false;

    public boolean isRooted(){
        return isRooted;
    }

    public void execute(String... cmds){
        for(String cmd : cmds){
            execute(cmd);
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void execute(String cmd){
        try {
            writer.writeBytes(cmd + "\n");
            writer.flush();
        }catch (IOException e){

        }
    }

    public void close(){
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommandExecuter() {
        try {
            process = Runtime.getRuntime().exec("su");
            isRooted = true;
            writer = new DataOutputStream(process.getOutputStream());
        }catch (IOException e){
            e.printStackTrace();
            isRooted = false;
        }
    }
}
