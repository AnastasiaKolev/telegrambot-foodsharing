package hackit;

import hackit.bot.TelegramBot;
import hackit.configuration.ApplicationConfig;
import hackit.cron.SendNotification;
import org.jetbrains.annotations.NotNull;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Launcher {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        TelegramBot bot = new TelegramBot();
        try {
            botsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        final ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        @NotNull final TelegramBot bootstrap = context.getBean(TelegramBot.class);
        try {
            Class.forName("org.postgresql.Driver");
            //on classpath
            System.out.println("on classpath ++++++++++++");
        } catch(ClassNotFoundException e) {
            // not on classpath
            System.out.println("not on classpath ------------");
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

}