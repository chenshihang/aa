package org.wing.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wing.common.Common;
import org.wing.common.ResponseCode;
import org.wing.common.ResultMap;
import org.wing.dao.*;
import org.wing.entity.*;
import org.wing.service.StudentService;
import org.wing.utils.CommonUtil;
import org.wing.viewobject.Articlevo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * 学生服务层接口实现层
 */
@Service
public class StudentServiceImpl implements StudentService{

    @Autowired
    private AchievementDao achievementDao;

    @Autowired
    private StudentInfoDao studentInfoDao;

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private ExaminationDao examinationDao;
    @Autowired
    private ClassQueryDao classQueryDao;
    @Autowired
    private StudentSelectDao studentSelectDao;
    @Autowired
    private ComputerGradeTwoDao computerGradeTwoDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleCategoryDao articleCategoryDao;
    /**
     * 根据学号和身份证号验证学生是否存在
     * @param studentNumber
     * @param idCard
     * @return
     */
    public int judgeExistStudent(String studentNumber, String idCard) {
        if (idCard.equals(studentDao.getIdCardByStudentNumber(studentNumber))){
            return 1;
        }else {
            return 0;
        }
    }

    /**
     * 根据学号查询学生认证信息
     * @param studentNumber
     * @return
     */
    public Student getStudentByStudentNumber(String studentNumber) {
        return studentDao.getStudentByStudentNumber(studentNumber);
    }

    /**
     * 注册学生认证信息
     * @param student
     */
    public void insertStudent(Student student) {
        studentDao.insertStudent(student);
    }

    /**
     * 更新学生认证信息
     * @param student
     */
    public void updateStudent(Student student) {

    }

    /**
     * 学生根据学号查询考试安排
     * @param studentNumber
     * @return
     */
    public List<Examination> getExamByStudentNumber(String studentNumber) {
        return examinationDao.getExamByStudentNumber(studentNumber);
    }
    /**
     * 根据考试周次，星期，节数，教室查找，并按学好排序
     * @param examination
     * @return
     */
    @Override
    public List<Examination> getExamByMap(Examination examination) {
        return examinationDao.getExamByMap(examination);
    }

    /**
     * 根据学生学号查询课程编号
     * @param studentNumber
     * @return
     */
    @Override
    public List<String> getCourseNumber(String studentNumber) {
        return studentSelectDao.getCourseNumber(studentNumber);
    }

    /**
     * 根据课程编号和学期查询课程信息
     * @param courseNumber
     * @return
     */
    @Override
    public ClassQuery getClassQuery(String courseNumber,String term) {
        return classQueryDao.getClassQuery(courseNumber,term);
    }

    @Override
    public List<ComputerGradeTwo> getComputerGradeTwo(String studentNumber) {
        return computerGradeTwoDao.getComputerGradeTwo(studentNumber);
    }

    public  List<Article> getArticles(){

        List<Article> articles = articleDao.getRecentArticles();
        return articles;
    }

    public Article getArticleById(Integer id){
        Article article = articleDao.selectByPrimaryKey(id);
        return article;
    }

    public StudentInfo getCurrentStudent(HttpServletRequest request){
        HttpSession session = request.getSession();
        StudentInfo studentInfo = (StudentInfo)session.getAttribute(Common.CURRENT_STUDENT);
        return studentInfo;
    }

    public List<String> getTermsByStuNum(String studentNumber){
        List<Achievement>  achievements = achievementDao.getTermNumber(studentNumber);
        List<String> result = new ArrayList<>();
        for(Achievement achievement: achievements){
            result.add(achievement.getTerm());
        }
        return result;
    }

    public List<Achievement> getGrades(String studentNumber,String term){

        List<Achievement> result = achievementDao.getAchievement(studentNumber,term);
        return result;
    }

    /**
     * 判断该学号的是否存在于学生认证信息表中
     * @param studentNumber
     * @return
     */
    public boolean studentIsExistInTable1(String studentNumber){
        int count =  studentDao.studentIsExistInTable1(studentNumber);
        if(count==0){
            return false;
        }else {
            return true;
        }
    }

    public StudentInfo getStudentInfo(String studentNumber) {
        return studentInfoDao.getStudentInfo(studentNumber);
    }

    public boolean updatePassword(String password,String studentNumber){

        int count = studentDao.updatePassword(password,studentNumber);
        if(count>0){
            return true;
        }else {
            return false;
        }
    }


    public ResultMap<PageInfo<Articlevo>> getArticlesByCategoryId(int categoryId,int pageNum,int pageSize){
        ArticleCategory category = articleCategoryDao.selectByPrimaryKey(categoryId);
        if(category==null){
            return ResultMap.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"文章分类参数错误");
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Article> articles = articleDao.getArticleList(categoryId);
        List<Articlevo> articleVos= new ArrayList<>();
        for(Article item:articles){
            articleVos.add(CommonUtil.articleToVo(item));
        }
        PageInfo<Articlevo> pageInfo = new PageInfo(articles);
        pageInfo.setList(articleVos);
        return ResultMap.createBySuccess(pageInfo);
    }


}
