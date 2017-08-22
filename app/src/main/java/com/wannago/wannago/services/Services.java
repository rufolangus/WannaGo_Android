package com.wannago.wannago.services;
import android.util.Log;
import com.wannago.wannago.services.Account.Account;
import com.wannago.wannago.services.Events.Events;
import com.wannago.wannago.services.Venues.Venues;

import java.lang.reflect.Field;
/**
 * Created by Rafael Valle on 3/22/2017.
 */

public final class Services
{
    public static Account Account;
    public static Events Events;
    public static Venues Venues;

    private static Services instance;

    private Services() {

    }

    public static void Init()
    {
        //Private singleton code
        if (instance == null)
            instance = new Services();

        //Using reflection to init services.
        Class thisClass = Services.class;
        Field[] fields = thisClass.getFields();
        for (int i = 0; i < fields.length; i++)
        {
            Field field = fields[i];
            Class<?> type = field.getType();
            Class<?>[] interfaces = type.getInterfaces();
            for (Class<?> interf : interfaces)
            {
                if (interf == IService.class)
                {
                    try {
                        IService service = (IService) field.get(instance);
                        if (service == null)
                        {
                            try
                            {
                                service = (IService) type.newInstance();
                                service.Init();
                                field.set(instance,service);
                                Log.d("SUCCESS", "Created new Service");
                            } catch (InstantiationException e)
                            {
                                e.printStackTrace();
                                Log.d("ERROR", "Failed creating service.");
                            }
                        }else
                            {
                                service.Init();
                                Log.d("SUCCESS", "Created new Service!");
                            }
                    } catch (IllegalAccessException e)
                    {
                        Log.d("ERROR", "Couldn't get IService from type");
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
