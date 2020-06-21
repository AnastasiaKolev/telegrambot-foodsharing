package hackit;

import hackit.alerts.AlertsHandler;
import hackit.bot.TelegramBot;
import hackit.cron.SendNotification;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Launcher {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        TelegramBot bot = new TelegramBot();
        try {
            botsApi.registerBot(bot);

            startAlerts(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        /* Schedule tasks not related to updates via Quartz */
        try {
            /* Instantiate the job that will call the hackit.bot function */
            JobDetail jobSendNotification = JobBuilder.newJob(SendNotification.class)
                    .withIdentity("sendNotification")
                    .build();

            /* Define a trigger for the call */
            Trigger trigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity("everyMorningAt8")
                    .withSchedule(
                            CronScheduleBuilder.dailyAtHourAndMinute(8, 0)) //TODO: define your schedule
                    .build();

            /* Create a scheduler to manage triggers */
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.getContext().put("hackit/bot", bot);
            scheduler.start();
            scheduler.scheduleJob(jobSendNotification, trigger);

        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private static void startAlerts(TelegramBot bot) {
        AlertsHandler alerts = new AlertsHandler() {
            @Override
            public void executeAlert(SendMessage msg) {
                try {
                    bot.execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        };
        alerts.startAlertTimers();
    }
}