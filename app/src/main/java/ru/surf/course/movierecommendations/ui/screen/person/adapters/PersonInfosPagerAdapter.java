package ru.surf.course.movierecommendations.ui.screen.person.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ru.surf.course.movierecommendations.R;
import ru.surf.course.movierecommendations.domain.people.Person;
import ru.surf.course.movierecommendations.ui.screen.personCredits.PersonCreditsFragmentView;
import ru.surf.course.movierecommendations.ui.screen.personInfo.PersonInfoFragmentView;


public class PersonInfosPagerAdapter extends FragmentStatePagerAdapter {

    private static final int PAGE_COUNT = 2;
    private Context mContext;
    private Person mPerson;

    public PersonInfosPagerAdapter(FragmentManager fm, Context context, Person person) {
        super(fm);
        mContext = context;
        mPerson = person;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PersonCreditsFragmentView.newInstance(mPerson);
            case 1:
                return PersonInfoFragmentView.newInstance(mPerson);
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getResources().getString(R.string.person_credits);
            case 1:
                return mContext.getResources().getString(R.string.info);
        }
        return null;
    }
}
