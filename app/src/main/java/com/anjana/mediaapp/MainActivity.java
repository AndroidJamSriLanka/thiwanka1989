package com.anjana.mediaapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.anjana.mediaapp.util.BitmapUtil;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private static final int REQUEST_CODE = 1;
    private static final ArrayList<Uri> bitmaps = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new StartFragment())
                    .commit();
        }


    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class StartFragment extends Fragment {


        public boolean onKeyDown(int keyCode, KeyEvent event)
        {
            if(keyCode == KeyEvent.KEYCODE_BACK)
            {
                Intent intent = new Intent(Intent.CATEGORY_APP_GALLERY);
                intent.addCategory(Intent.CATEGORY_APP_BROWSER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }
            return false;
        }


        public StartFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }



        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            RelativeLayout rootLayout = (RelativeLayout) rootView.findViewById(R.id.root);
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
            return rootView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
                if (data.getData() != null) {
                    bitmaps.add(data.getData());
                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        ClipData.Item clipData = data.getClipData().getItemAt(i);
                        bitmaps.add(clipData.getUri());
                    }
                }
            super.onActivityResult(requestCode, resultCode, data);
            getFragmentManager().beginTransaction().replace(R.id.container, new GalleryFragement()).commit();
        }
    }

    public static class GalleryFragement extends Fragment {
        private int image_width;

        public GalleryFragement() {
            setHasOptionsMenu(true);

        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.menu_import, menu);
        }



        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId() == R.id.action_import)
            {
                Intent intent;
                intent = new Intent();
                intent.setType("audio/mpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            }


            return super.onOptionsItemSelected(item);
        }




        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (data.getData() != null) {
                MediaPlayer.create(getActivity(),data.getData()).start();
            } else {
                MediaPlayer.create(getActivity(),data.getClipData().getItemAt(0).getUri()).start();
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
            final TableLayout rootLayout = (TableLayout) rootView.findViewById(R.id.root_table);
            rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    image_width = rootLayout.getWidth() / 2 - 2;
                    for (int i = 0; i < bitmaps.size(); i += 2) {
                        TableRow tableRow = new TableRow(getActivity());
                        tableRow.setGravity(Gravity.CENTER);
                        tableRow.setBackgroundColor(Color.BLACK);
                        tableRow.setPadding(5, 5, 5, 5);
                        tableRow.setWeightSum(1);
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(rootLayout.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
                        ImageView imageView1 = new ImageView(getActivity());
                        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(rootLayout.getWidth() / 2, ViewGroup.LayoutParams.MATCH_PARENT);
                        layoutParams.weight = 0.5f;
                        imageView1.setLayoutParams(layoutParams);
                        imageView1.setAdjustViewBounds(true);
                        try {
                            imageView1.setImageBitmap(BitmapUtil.getInstance().decodeSampledBitmapFromFile(getActivity(), bitmaps.get(i), image_width, image_width));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView1.setBackgroundColor(Color.BLUE);
                        final int i1 = i;
                        imageView1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putInt(TextFragment.class.getName(), i1);
                                TextFragment textFragment = new TextFragment();
                                textFragment.setArguments(bundle);
                                getFragmentManager().beginTransaction().addToBackStack(TextFragment.class.getName()).replace(R.id.container, textFragment).commit();
                            }
                        });
                        tableRow.addView(imageView1);
                        if (bitmaps.size() % 2 == 0) {
                            ImageView imageView2 = new ImageView(getActivity());
                            imageView2.setLayoutParams(layoutParams);
                            imageView2.setAdjustViewBounds(true);
                            try {
                                imageView2.setImageBitmap(BitmapUtil.getInstance().decodeSampledBitmapFromFile(getActivity(), bitmaps.get(i + 1), image_width, image_width));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            imageView2.setBackgroundColor(Color.RED);
                            final int i2 = i + 1;
                            imageView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt(TextFragment.class.getName(), i2);
                                    TextFragment textFragment = new TextFragment();
                                    textFragment.setArguments(bundle);
                                    getFragmentManager().beginTransaction().addToBackStack(TextFragment.class.getName()).replace(R.id.container, textFragment).commit();
                                }
                            });
                            tableRow.addView(imageView2);
                        }
                        rootLayout.addView(tableRow);
                    }
                }
            });
            return rootView;
        }
    }

    public static class TextFragment extends Fragment {


        private InputMethodManager mgr;
        private EditText editText;
        private ImageView imageView;
        private TextView textView1;
        private TextView textView2;
        private TextView current;

        public TextFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_text, container, false);
            imageView = (ImageView) rootView.findViewById(R.id.image);
            editText = (EditText) rootView.findViewById(R.id.picture_edit);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    current.setText(editText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    try {
                        imageView.setImageBitmap(BitmapUtil.getInstance().decodeSampledBitmapFromFile(getActivity(), bitmaps.get(getArguments().getInt(TextFragment.class.getName(), 0)), imageView.getWidth(), imageView.getHeight()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            textView1 = (TextView) rootView.findViewById(R.id.picture_text_1);
            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getVisibility() == View.GONE || current != textView1) {
                        current = textView1;
                        editText.setVisibility(View.VISIBLE);
                        editText.requestFocus();
                        mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        mgr.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        editText.clearFocus();
                        editText.setVisibility(View.GONE);
                    }
                    if (textView1.getText() == "") {
                        editText.getText().clear();
                    } else {
                        editText.setText(textView1.getText());
                    }
                }
            });
            textView2 = (TextView) rootView.findViewById(R.id.picture_text_2);
            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getVisibility() == View.GONE || current != textView2) {
                        current = textView2;
                        editText.setVisibility(View.VISIBLE);
                        editText.requestFocus();
                        mgr.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    } else {
                        mgr.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        editText.clearFocus();
                        editText.setVisibility(View.GONE);
                    }
                    if (textView2.getText() == "") {
                        editText.getText().clear();
                    } else {
                        editText.setText(textView2.getText());
                    }
                }
            });

            return rootView;

        }
    }
}
