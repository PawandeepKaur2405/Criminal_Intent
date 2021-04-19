package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

   private ViewPager mViewPager;
   private List<Crime> mCrimes;
   private Button mFirstButton;
   private Button mLastButton;

    private static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId)
    {
        Intent intent = new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mViewPager = (ViewPager)findViewById(R.id.crime_view_pager);
        mLastButton = (Button)findViewById(R.id.last_button);
        mFirstButton = (Button)findViewById(R.id.first_button);

        mCrimes = CrimeLab.get(this).getCrimes();

        UUID crimeId = (UUID)getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
               return CrimeFragment.newInstance(crime.getMid());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        
        for(int i=0;i<mCrimes.size();i++)
        {
            if(mCrimes.get(i).getMid().equals(crimeId))
            {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(mViewPager.getCurrentItem()==0)
                {
                    mFirstButton.setVisibility(View.INVISIBLE);
                }
                else
                {
                    mFirstButton.setVisibility(View.VISIBLE);
                }

                if(mViewPager.getCurrentItem()== ((mViewPager.getAdapter().getCount())-1))
                {
                    mLastButton.setVisibility(View.INVISIBLE);
                }
                else
                {
                    mLastButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });
        mLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewPager.setCurrentItem((mViewPager.getAdapter().getCount())-1);
            }
        });


    }
}
