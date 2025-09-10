package com.Pojo;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class MusicPlayer {

    static public class MusicServer{
        private Clip clip;
        //加载音频文件
        public void loading(String MusicFilePath){
            try{
                File file = new File(MusicFilePath);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        public void play(){
            if(clip != null){
                clip.start();
            }
        }
        //循环播放
        public void loop(){
            if(clip != null){
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        //设置音量
        public void SetVolumn(double volumn){
            if(volumn < 0) volumn = 0;
            else if(volumn > 1) volumn = 1;
            FloatControl floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float Db = (float)(Math.log(volumn)/Math.log(10) * 20);
            floatControl.setValue(Db);
        }
        public void  playing(MusicServer musicServer){
            musicServer.loading("背景音乐.wav");
            musicServer.SetVolumn(0.5);
            new Thread(()->{
                try{
                    musicServer.play();
                    musicServer.loop();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }).start();
            try{
                Thread.sleep(10000000000000000L);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        public  void stoping(MusicServer musicServer){
            musicServer.SetVolumn(0);
        }
    }


}

