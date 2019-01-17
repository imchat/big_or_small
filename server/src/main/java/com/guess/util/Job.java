package com.guess.util;

import com.guess.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;

public class Job {
    @Autowired
    GameService gameService;

    public Job() {

        System.out.println("start jobs...............");

    }

    public void run() {
        try {
            System.out.println("updateGameRund start run..............." + System.currentTimeMillis());
            gameService.updateGameRund();
            System.out.println("updateGameRund  end run...............");
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
