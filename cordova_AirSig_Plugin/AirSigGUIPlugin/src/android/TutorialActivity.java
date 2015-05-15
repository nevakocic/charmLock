package com.airsig.webclientgui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TutorialActivity extends ActivityExtension {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private ImageView mImageViewPage0;
    private ImageView mImageViewPage1;
    private ImageView mImageViewPage2;
    private ImageView mImageViewPage3;
    private Button mButtonSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((getIdentifier("activity_tutorial", "layout")));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(getIdentifier("pager", "id"));
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mButtonSkip = (Button) findViewById(getIdentifier("button_get_started", "id"));
        mButtonSkip.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }

        });

        mImageViewPage0 = (ImageView) findViewById(getIdentifier("imageView_p0", "id"));
        mImageViewPage1 = (ImageView) findViewById(getIdentifier("imageView_p1", "id"));
        mImageViewPage2 = (ImageView) findViewById(getIdentifier("imageView_p2", "id"));
        mImageViewPage3 = (ImageView) findViewById(getIdentifier("imageView_p3", "id"));

        mImageViewPage0.setImageDrawable(getDrawable("dot_indicator_hi"));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int page) {
                mImageViewPage0.setImageDrawable(getDrawable("dot_indicator_lo"));
                mImageViewPage1.setImageDrawable(getDrawable("dot_indicator_lo"));
                mImageViewPage2.setImageDrawable(getDrawable("dot_indicator_lo"));
                mImageViewPage3.setImageDrawable(getDrawable("dot_indicator_lo"));

                switch (page) {
                    case 0:
                        mImageViewPage0.setImageDrawable(getDrawable("dot_indicator_hi"));
                        break;
                    case 1:
                        mImageViewPage1.setImageDrawable(getDrawable("dot_indicator_hi"));
                        break;
                    case 2:
                        mImageViewPage2.setImageDrawable(getDrawable("dot_indicator_hi"));
                        break;
                    case 3:
                        mImageViewPage3.setImageDrawable(getDrawable("dot_indicator_hi"));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class
            // below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate((getResources().getIdentifier("fragment_tutorial", "layout", inflater.getContext().getPackageName())), container, false);

            Bundle args = getArguments();
            int pageNumber = -1;
            ImageView image = (ImageView) rootView.findViewById(getResources().getIdentifier("imageView_image", "id", inflater.getContext().getPackageName()));
            TextView title = (TextView) rootView.findViewById(getResources().getIdentifier("textView_title", "id", inflater.getContext().getPackageName()));
            ;
            TextView description = (TextView) rootView.findViewById(getResources().getIdentifier("textView_description", "id", inflater.getContext().getPackageName()));
            boolean condition = (null != image && null != title && null != description);
            if (condition && null != args && -1 != (pageNumber = args.getInt(ARG_SECTION_NUMBER, -1))) {
                switch (pageNumber) {
                    case 0:
                        image.setImageDrawable(getResources().getDrawable((getResources().getIdentifier("img_tutorial_01", "drawable", inflater.getContext().getPackageName()))));
                        title.setText((getResources().getIdentifier("tutorial_title_p0", "string", inflater.getContext().getPackageName())));
                        description.setText((getResources().getIdentifier("tutorial_desc_p0", "string", inflater.getContext().getPackageName())));
                        break;
                    case 1:
                        image.setImageDrawable(getResources().getDrawable((getResources().getIdentifier("img_tutorial_02", "drawable", inflater.getContext().getPackageName()))));
                        title.setText((getResources().getIdentifier("tutorial_title_p1", "string", inflater.getContext().getPackageName())));
                        description.setText((getResources().getIdentifier("tutorial_desc_p1", "string", inflater.getContext().getPackageName())));
                        break;
                    case 2:
                        image.setImageDrawable(getResources().getDrawable((getResources().getIdentifier("img_tutorial_03", "drawable", inflater.getContext().getPackageName()))));
                        title.setText((getResources().getIdentifier("tutorial_title_p2", "string", inflater.getContext().getPackageName())));
                        description.setText((getResources().getIdentifier("tutorial_desc_p2", "string", inflater.getContext().getPackageName())));
                        break;
                    case 3:
                        image.setImageDrawable(getResources().getDrawable((getResources().getIdentifier("img_tutorial_04", "drawable", inflater.getContext().getPackageName()))));
                        title.setText((getResources().getIdentifier("tutorial_title_p3", "string", inflater.getContext().getPackageName())));
                        description.setText((getResources().getIdentifier("tutorial_desc_p3", "string", inflater.getContext().getPackageName())));
                        break;
                    default:
                        break;
                }
            }

            return rootView;
        }
    }

}
