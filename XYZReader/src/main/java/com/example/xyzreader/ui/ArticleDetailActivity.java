package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.data.ItemsContract;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.Map;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private Cursor mCursor;
    private long mStartId;
    Target mTarget;


    int mBannerColor;

    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;
    ImageView mArticleArt;
    private Map<Integer, Fragment> myFragmentMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        setContentView(R.layout.activity_detail);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarDetail);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setTitle(null);
        mArticleArt = (ImageView) findViewById(R.id.articleArt);
        myFragmentMap = new HashMap<>();


        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new MyPagerAdapter(getFragmentManager());
        mPager = (ViewPager) findViewById(R.id.viewPager);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPageMargin((int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mPager.setPageMarginDrawable(new ColorDrawable(0x22000000));
        mPager.setPageTransformer(true, new MyPageTransformer());

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mCursor != null) {
                    mCursor.moveToPosition(position);
                    setUpHeader(mCursor.getString((ArticleLoader.Query.PHOTO_URL)), mCursor.getString(ArticleLoader.Query.TITLE));
                    setUpShareButton(mCursor.getString(ArticleLoader.Query.TITLE), mCursor.getString(ArticleLoader.Query.AUTHOR));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = ItemsContract.Items.getItemId(getIntent().getData());
            }
        } else {
            mStartId = savedInstanceState.getLong("index");
        }

    }

    private void setUpHeader(String imageUrl, String title) {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarDetail);
        collapsingToolbarLayout.setTitle(title);
        mTarget = new Target() {
            @SuppressWarnings("deprecation")
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (bitmap != null) {
                    mArticleArt.setImageBitmap(bitmap);
                    Palette palette = Palette.from(bitmap).generate();
                    mBannerColor = palette.getDarkVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimary)));
                    collapsingToolbarLayout.setContentScrim(new ColorDrawable(mBannerColor));
                    Fragment page = mPagerAdapter.getFragment(mPager.getCurrentItem());
                    if (page != null) {
                        ((ArticleDetailFragment) page).setUpBannerColor(mBannerColor);
                    }
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                mArticleArt.setImageDrawable(placeHolderDrawable);
            }
        };
        Picasso.with(this)
                .load(imageUrl)
                .placeholder(new ColorDrawable(Color.WHITE))
                .into(mTarget);

    }

    private void setUpShareButton(final String title, final String author) {
        findViewById(R.id.share_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_text),
                        title,
                        author));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newAllArticlesInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        // Select the start ID
        if (mStartId > 0) {
            mCursor.moveToFirst();
            while (!mCursor.isAfterLast()) {
                if (mCursor.getLong(ArticleLoader.Query._ID) == mStartId) {
                    final int position = mCursor.getPosition();
                    mPager.setCurrentItem(position, false);
                    setUpHeader(mCursor.getString(ArticleLoader.Query.PHOTO_URL), mCursor.getString(ArticleLoader.Query.TITLE));
                    setUpShareButton(mCursor.getString(ArticleLoader.Query.TITLE), mCursor.getString(ArticleLoader.Query.AUTHOR));
                    break;
                }
                mCursor.moveToNext();
            }
            mStartId = 0;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        mPagerAdapter.notifyDataSetChanged();
    }


    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            Fragment myFragment = ArticleDetailFragment.newInstance(mCursor.getLong(ArticleLoader.Query._ID));
            myFragmentMap.put(position, myFragment);
            return myFragment;
        }

        public ArticleDetailFragment getFragment(int key) {
            return ((ArticleDetailFragment) myFragmentMap.get(key));
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            myFragmentMap.remove(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment myFragment = (Fragment) super.instantiateItem(container, position);
            if (!myFragmentMap.containsKey(position)) {
                myFragmentMap.put(position, myFragment);
            }
            return myFragment;
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCursor != null) {
            outState.putLong("index", mCursor.getLong(ArticleLoader.Query._ID));
        }
    }
}
