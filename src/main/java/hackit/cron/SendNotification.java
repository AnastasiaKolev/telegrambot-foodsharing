package hackit.cron;

import hackit.bot.TelegramBot;
import org.quartz.*;

public class SendNotification implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        /* Retrieve the hackit.bot instance */
        SchedulerContext schedulerContext = null;
        try {
            schedulerContext = jobExecutionContext.getScheduler().getContext();
        } catch (SchedulerException e1) {
            e1.printStackTrace();
        }
        TelegramBot bot = (TelegramBot) schedulerContext.get("hackit/bot");

        bot.sendNotification();

    }

}

