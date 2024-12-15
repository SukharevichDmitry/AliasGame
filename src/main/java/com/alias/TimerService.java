package com.alias;

import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService {
    private int timeRemaining;  // Оставшееся время
    private Timer timer;        // Таймер
    private Runnable onTimeUp;  // Действие по истечении времени
    private Runnable onTick;    // Действие каждую секунду

    // Конструктор
    public TimerService(int durationSeconds, Runnable onTimeUp, Runnable onTick) {
        this.timeRemaining = durationSeconds;  // Устанавливаем начальное время
        this.onTimeUp = onTimeUp;              // Действие при окончании таймера
        this.onTick = onTick;                  // Действие каждую секунду
    }

    // Метод для запуска таймера
    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining <= 0) {
                    Platform.runLater(onTimeUp);  // Если время вышло, вызываем onTimeUp
                    timer.cancel();  // Останавливаем таймер
                    timer = null;     // Убираем ссылку на таймер
                } else {
                    timeRemaining--;  // Уменьшаем оставшееся время
                    Platform.runLater(onTick);  // Вызываем onTick каждую секунду
                }
            }
        }, 0, 1000);  // Таймер будет срабатывать каждую секунду
    }

    // Метод для получения оставшегося времени
    public int getTimeRemaining() {
        return timeRemaining;
    }

    // Метод для остановки таймера
    public void stop() {
        if (timer != null) {
            timer.cancel();  // Останавливаем таймер
            timer = null;    // Убираем ссылку на таймер
        }
    }

    public void resetTime(int newTime) {
        this.timeRemaining = newTime;  // Обновляем оставшееся время
        stop();  // Останавливаем текущий отсчет времени
        start();  // Запускаем новый отсчет времени
    }
}
