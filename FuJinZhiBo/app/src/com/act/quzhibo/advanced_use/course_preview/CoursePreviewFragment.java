package com.act.quzhibo.advanced_use.course_preview;

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
import com.act.quzhibo.advanced_use.data_access.GetCoursePreviews;
import com.act.quzhibo.advanced_use.model.CoursePreviewInfo;
import com.act.quzhibo.util.ToastUtil;

import java.util.List;

/**
 * 课程预览界面
 */

public class CoursePreviewFragment extends Fragment {


    private RecyclerView mRvCoursePreview;
    private CoursePreviewAdapter mCoursePreviewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = getView();

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.advanced_use__fragment_course_preview, null);

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

    public static class CoursePreviewItemDecoration extends RecyclerView.ItemDecoration {

        private int margin;

        public CoursePreviewItemDecoration(Context context) {
            margin = context.getResources().getDimensionPixelSize(R.dimen
                    .advanced_use___course_preview_item_decoration_margin);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(margin, margin, margin, margin);
        }

    }
}
