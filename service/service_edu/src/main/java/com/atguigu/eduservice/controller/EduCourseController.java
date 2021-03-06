package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.TeachQuery;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author testjava
 * @since 2021-10-15
 */
@RestController
@RequestMapping("/eduservice/edu-course")
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    /**
     * 分页条件查询课程列表
     * @return
     */
    @PostMapping("pageCourseCondition/{current}/{limit}")
    public R getCourseList(@PathVariable long current,
                           @PathVariable long limit,
                           @RequestBody(required = false) EduCourse course){
        //创建Page对象
        Page<EduCourse> pageCourse = new Page<>(current,limit);
        //构造条件
        LambdaQueryWrapper<EduCourse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EduCourse::getIsDeleted,0);
        String title = course.getTitle();
        Integer lessonNum = course.getLessonNum();
        Date gmtCreate = course.getGmtCreate();
        Integer status = course.getStatus();
        if(!StringUtils.isEmpty(title)){
            wrapper.like(EduCourse::getTitle,title);
        }
        if(!StringUtils.isEmpty(status)){
            wrapper.like(EduCourse::getStatus,status);
        }
        if(!StringUtils.isEmpty(lessonNum)){
            wrapper.like(EduCourse::getLessonNum,lessonNum);
        }
        if(!StringUtils.isEmpty(gmtCreate)){
            wrapper.like(EduCourse::getGmtCreate,gmtCreate);
        }
        //调用方法实现条件查询分页
        courseService.page(pageCourse, wrapper);
        long total = pageCourse.getTotal();
        List<EduCourse> records = pageCourse.getRecords();
        return R.ok().data("total",total).data("records",records);
    }

    /**
     * 增加课程信息
     * @param courseInfoVo
     * @return
     */
    @PostMapping("addCourse")
    public R addCourse(@RequestBody CourseInfoVo courseInfoVo){
        String id = courseService.saveCourseInfo(courseInfoVo);
        return R.ok().data("courseId",id);
    }
    /**
     * 根据课程id查询课程基本信息
     * @param courseId
     * @return
     */
    @GetMapping("/getCourseInfoByCourseId/{courseId}")
    public R getCourseInfoByCourseId(@PathVariable String courseId){
        CourseInfoVo courseInfoVo = courseService.getCourseInfoByCourseId(courseId);;
        System.out.println(courseInfoVo.getDescription());
        return R.ok().data("courseInfoVo",courseInfoVo);
    }

    /**
     * 更新课程信息
     * @param courseInfoVo
     * @return
     */
    @PostMapping("/updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        courseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }

    /**
     * 根据课程id获得要发布的课程信息
     * @param courseId
     * @return
     */
    @GetMapping("/getPublishCourseInfo/{courseId}")
    public R getPublishCourseInfo(@PathVariable String courseId){
        CoursePublishVo coursePublishVo = courseService.publishCourseInfo(courseId,0);
        return R.ok().data("publishCourse",coursePublishVo);
    }

    /**
     * 课程最终发布
     * @param courseId
     * @return
     */
    @PostMapping("publishCourse/{courseId}")
    public R publishCourse(@PathVariable String courseId){
        EduCourse course = new EduCourse();
        course.setId(courseId);
        course.setStatus(1);
        boolean bool = courseService.updateById(course);
        return bool?R.ok():R.error();
    }

    @DeleteMapping("deleteCourse/{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        EduCourse one = courseService.getOne(new LambdaQueryWrapper<EduCourse>().eq(EduCourse::getId, courseId));
        if(one != null || !Objects.equals(one.getIsDeleted(),0)){
            one.setIsDeleted(1);
            courseService.updateById(one);
        }
        return R.ok();
    }
}

