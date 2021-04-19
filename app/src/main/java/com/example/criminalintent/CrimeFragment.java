package com.example.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

import static com.example.criminalintent.R.id.crime_camera;
import static com.example.criminalintent.R.id.crime_photo;


public class CrimeFragment extends Fragment
{

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mSuspectPhone;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    public static CrimeFragment newInstance(UUID crimeId)   //MainActivity will call this instead of constructor to create crime fragment
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId); //adding arguments to bundle

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);    //attaching argument bundle to fragment
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);  //Retrieving Arguments
       mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

       mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_crime,container,false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton  = (Button)v.findViewById(R.id.crime_data);
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager manager = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance( mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,
                        REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });

        mReportButton = (Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND); //IMPLICIT INTENT
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT , getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT , getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent , getString(R.string.crime_report));
                startActivity(intent);

            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = (Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setText(R.string.crime_suspect_text);

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        String phone = mCrime.getSuspectPhone();
        Uri number = Uri.parse("tel:" + phone);

        final Intent callContact = new Intent(Intent.ACTION_DIAL,
                number);

        callContact.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        callContact.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        mSuspectPhone = (Button)v.findViewById(R.id.crime_suspect_call);
        mSuspectPhone.setText(R.string.call_suspect);

        mSuspectPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(callContact);
            }
        });

        if(mCrime.getSuspect() != null)
        {
            String name = mCrime.getSuspect();
            mSuspectButton.setText("Suspect : " + name);
            mSuspectPhone.setText("Call " + name);
        }


        //to check contact app's availibility
        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,
                packageManager.MATCH_DEFAULT_ONLY)==null)
        {
            mSuspectButton.setEnabled(false);
            mSuspectPhone.setEnabled(false);
        }

        mPhotoButton = (ImageButton)v.findViewById(crime_camera);
        mPhotoView = (ImageView)v.findViewById(crime_photo);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //checking camera  app and photo location's availbility
        boolean canTakePhoto = mPhotoFile !=null && captureImage.resolveActivity(packageManager) != null;

        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri uri = FileProvider.getUriForFile(getActivity() ,
                        "com.example.criminalintent.fileprovider" ,
                        mPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT , uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage ,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo activity : cameraActivities)
                {
                    getActivity().grantUriPermission(activity.activityInfo.packageName
                    ,uri , Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage , REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView)v.findViewById(crime_photo);

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                FragmentManager manager = getFragmentManager();
                PhotoViewerFragment dialog = PhotoViewerFragment.newInstance(mPhotoFile);
                dialog.show(manager , DIALOG_PHOTO);

            }
        });

        updatePhotoView();

        return v;
    }

    //for date
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode != Activity.RESULT_OK)
        {
            return;
        }
        if(requestCode == REQUEST_DATE)
        {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        }
        else if(requestCode == REQUEST_CONTACT && data!=null)
        {
            Uri contactUri = data.getData();
            String queryFields[] = new String[]
                    {
                            ContactsContract.Contacts.DISPLAY_NAME,
                    };

            String queryFieldId[] = new String[]
                    {
                            ContactsContract.Contacts._ID
                    };

            Cursor c = getActivity().getContentResolver().query(contactUri,queryFields,null,null,null);

            try
            {
                if(c.getCount() == 0)
                    return;
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            }
            finally
            {
                c.close();
            }

            Cursor cID = getActivity().getContentResolver().query(contactUri,queryFieldId,null,null,null);

            try
            {
                if(cID.getCount() == 0)
                    return;
                cID.moveToFirst();
                String contactID = cID.getString(0);
                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                String[] phoneNumberQueryFields = new String[]{
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                };

                String phoneWhereClause = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";

                String phoneQueryParameters[] = new String[]
                        {
                          contactID
                        };

                Cursor phoneCursor = getActivity().getContentResolver().query(phoneUri , phoneNumberQueryFields ,
                        phoneWhereClause,phoneQueryParameters,null);

                try
                {
                    if(phoneCursor.getCount() == 0)
                        return;

                    phoneCursor.moveToFirst();
                    String phoneValue = phoneCursor.getString(0);
                    mCrime.setSuspectPhone(phoneValue);
                    mSuspectPhone.setText("Call " + mCrime.getSuspect());
                }
                finally
                {
                    phoneCursor.close();
                }
            }
            finally
            {
                cID.close();
            }
        }
        else if(requestCode == REQUEST_PHOTO)
        {
            Uri uri = FileProvider.getUriForFile(getActivity() ,
                    "com.example.criminalintent.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    //to generate crime report
    private String getCrimeReport()
    {
        String solvedString = null;
        if(mCrime.isSolved())
        {
            solvedString = getString(R.string.crime_report_solved);
        }
        else
        {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE , MMM dd";
        String dateString =  DateFormat.format(dateFormat,
                    mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if(suspect == null)
        {
            suspect = getString(R.string.crime_report_noSuspect);
        }
        else
        {
            suspect = getString(R.string.crime_report_suspect);
        }

        String report = getString(R.string.crime_report , mCrime.getTitle() , dateString , solvedString  , suspect);
        return report;
    }

    private void updatePhotoView()
    {
        if(mPhotoFile == null || !mPhotoFile.exists())
        {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(
                    getString(R.string.crime_photo_no_image_description));
        }
        else
        {
            Bitmap bitmap = PhotoUtils.getScaledBitmap(mPhotoFile.getPath() , getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(
                    getString(R.string.crime_photo_image_description));
        }
    }


    //to update database
    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }
}
