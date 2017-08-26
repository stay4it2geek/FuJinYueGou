package com.act.quzhibo.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.act.quzhibo.R;
import com.act.quzhibo.adapter.CoursePreviewAdapter;
import com.act.quzhibo.data_access.GetCoursePreviews;
import com.act.quzhibo.entity.CoursePreviewInfo;
import com.act.quzhibo.util.ToastUtil;

import java.util.List;


public class VideosPreviewFragment extends BackHandledFragment {


    private RecyclerView mRvCoursePreview;
    private CoursePreviewAdapter mCoursePreviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = getView();

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_course_preview, null);

            mRvCoursePreview = (RecyclerView) rootView.findViewById(R.id.rvCoursePreview);
            mRvCoursePreview.addItemDecoration(new CoursePreviewItemDecoration(getActivity()));
            mRvCoursePreview.setHasFixedSize(true);
            mRvCoursePreview.setLayoutManager(new GridLayoutManager(getActivity(), 2));

            if (mCoursePreviewAdapter != null) {
                mCoursePreviewAdapter.release();
            }
            mCoursePreviewAdapter = new CoursePreviewAdapter(getActivity(),null);
            mRvCoursePreview.setAdapter(mCoursePreviewAdapter);

            initCoursePreviewData();
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCoursePreviewAdapter != null) {
            mCoursePreviewAdapter.release();
        }
    }

    private void initCoursePreviewData() {
        GetCoursePreviews getCoursePreviews = new GetCoursePreviews();
        getCoursePreviews.getCoursePreviews(getActivity(), new GetCoursePreviews.OnGetCoursePreviewsListener() {

            @Override
            public void onGetCoursePreviewsSucceed(List<CoursePreviewInfo> coursePreviewInfos) {
                mCoursePreviewAdapter.update(getActivity(),coursePreviewInfos);
            }

            @Override
            public void onGetCoursePreviewsFailed() {
                ToastUtil.showToast(getActivity(), getActivity().getString(R.string.common__get_data_failed));
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public static class CoursePreviewItemDecoration extends RecyclerView.ItemDecoration {

        private int margin;

        public CoursePreviewItemDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen
                    ._course_preview_item_decoration_margin);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(margin, margin, margin, margin);
        }

    }
}
