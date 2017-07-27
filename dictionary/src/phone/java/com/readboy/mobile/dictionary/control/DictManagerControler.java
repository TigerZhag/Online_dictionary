package com.readboy.mobile.dictionary.control;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.readboy.mobile.dictionary.fragment.DictSearchFragment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by Senny on 2015/10/27.
 */
public class DictManagerControler {

    private static DictManagerControler instance;
    private ExecutorService singleExecutor;
    private Intent hideInputIntent;

    DictManagerControler() {
        init();
    }

    public static DictManagerControler getInstance() {
        if (instance == null) {
            instance = new DictManagerControler();
        }
        return instance;
    }

    public static void addStackFragment(FragmentTransaction ft,
                                                int id, String stack, String tag, Fragment fragment) {
        addStackFragmentWithAnim(ft, id, stack, tag, fragment, 0, 0, 0, 0);
    }

    public static void addStackFragmentWithAnim(FragmentTransaction ft,
                                        int id, String stack, String tag, Fragment fragment,
                                        int enter, int exit, int popEnter, int popExit) {
        ft.addToBackStack(stack);
        ft.setCustomAnimations(enter, exit, popEnter, popExit);
        ft.add(id, fragment, tag);
        ft.commit();
    }

    public static void replaceFragment(FragmentTransaction ft, int id, String stack, String tag, Fragment fragment) {
        replaceFragmentWithAnim(ft, id, stack, tag, fragment, 0, 0, 0, 0);
    }

    public static void replaceFragmentWithAnim(FragmentTransaction ft,
                                               int id, String stack, String tag, Fragment fragment,
                                               int enter, int exit, int popEnter, int popExit) {
        ft.setCustomAnimations(enter, exit, popEnter, popExit);
        ft.replace(id, fragment, tag);
        ft.commit();
    }

    public static void addFragment(FragmentTransaction ft,
                                   int id, String stack, String tag, Fragment fragment) {
        addFragmentWithAnim(ft, id, stack, tag, fragment, 0, 0, 0, 0);
    }

    public static void addFragmentWithAnim(FragmentTransaction ft,
                                               int id, String stack, String tag, Fragment fragment,
                                               int enter, int exit, int popEnter, int popExit) {
        ft.setCustomAnimations(enter, exit, popEnter, popExit);
        ft.add(id, fragment, tag);
        ft.commit();
    }

    private void init() {
        singleExecutor = Executors.newSingleThreadExecutor();
        hideInputIntent = new Intent(DictSearchFragment.ACTION_HIDE_INPUT);
    }

    public void hideInput(Context context) {
        if (context != null) {
            context.sendBroadcast(hideInputIntent);
        }
    }

    public void execute(Runnable runable) {
        if (singleExecutor != null) {
            try {
                singleExecutor.execute(runable);
            } catch (RejectedExecutionException e1) {
                e1.printStackTrace();
            } catch (NullPointerException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void shutDown() {
        if (singleExecutor != null) {
            singleExecutor.shutdown();
        }
    }
}
