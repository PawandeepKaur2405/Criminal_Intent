package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerVew;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private class CrimeHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  //defining ViewHolder
    {

        private TextView mTitleTextView;    //binding of widgets
        private TextView mDateTextView;
        private ImageView mImageView;

        private Crime mCrime;


        public CrimeHolder(LayoutInflater inflater,ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime,parent,false));

            itemView.setOnClickListener(this);

            mTitleTextView = (TextView)itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView)itemView.findViewById(R.id.crime_date);
            mImageView = (ImageView)itemView.findViewById(R.id.image_view);
        }

        public void bind(Crime crime)
        {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
           Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getMid());    //openeing CrimePagerActivity
           startActivity(intent);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>    //defining adapter
    {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes)
        {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater,parent);

        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position)
        {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes)
        {
            mCrimes = crimes;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        mCrimeRecyclerVew = (RecyclerView)view
                .findViewById(R.id.recycler_view);
        mCrimeRecyclerVew.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCrimeRecyclerVew.setHasFixedSize(true);

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    public void updateUI()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(mAdapter == null )
        {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerVew.setAdapter(mAdapter);
        }
        else
        {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();

    }

    @Override
    public void onResume() {
        super.onResume();

        updateUI();
    }

    //inflating menu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible)
        {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else
        {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent  = CrimePagerActivity
                                .newIntent(getActivity(),crime.getMid());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();  //declares option menus has changed so should be recreated
                updateSubtitle();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }

    }

    private void updateSubtitle()
    {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle  = getString(R.string.subtitle_format,crimeCount);

        if(!mSubtitleVisible)
        {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    //telling CrimeListFragment that it need to answer menu call backs
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

}
