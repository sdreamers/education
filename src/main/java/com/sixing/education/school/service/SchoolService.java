package com.sixing.education.school.service;

import com.sixing.base.domain.base.HartsResult;
import com.sixing.base.domain.base.PageRecords;
import com.sixing.base.domain.base.PageVO;
import com.sixing.base.domain.packet.PacketVO;
import com.sixing.base.domain.school.SchoolPO;
import com.sixing.base.domain.school.SchoolQuery;
import com.sixing.base.domain.school.SchoolVO;
import com.sixing.base.utils.exception.ServiceException;

import java.util.List;

/**
* 学校的业务层接口
* @author 
* @date 2020年6月20日
*/
public interface SchoolService {

    /**
    * 新增学校
    *
    * @param insertParams 待新增的学校实例
    * @return 装填了自增型主键的学校对象
    * @throws ServiceException 业务异常
    */
    SchoolPO insert(SchoolPO insertParams) throws ServiceException;

    /**
    * 根据ID修改，返回修改结果
    *
    * @param setParams 待修改的学校实例
    * @param id 编号
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(SchoolPO setParams, Long id) throws ServiceException;

    /**
    * 修改学校，返回修改结果
    *
    * @param setParams 待修改的学校实例
    * @param whereParams 查询条件
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(SchoolPO setParams, SchoolQuery whereParams) throws ServiceException;

    /**
    * 修改学校，返回受影响的行数
    *
    * @param setParams 待修改的学校实例
    * @param whereParams 查询条件
    * @return 受影响的行数
    * @throws ServiceException 业务异常
    */
    Integer updateAndRtnRows(SchoolPO setParams, SchoolQuery whereParams) throws ServiceException;

    /**
    * 根据ID删除学校
    *
    * @param id 编号
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(Long id) throws ServiceException;

    /**
    * 根据ID数组，批量删除学校
    *
    * @param id 编号数组
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(Long[] id) throws ServiceException;

    /**
    * 根据查询条件，删除学校
    *
    * @param whereParams 查询条件
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(SchoolQuery whereParams) throws ServiceException;

    /**
    * 根据查询条件数组，批量删除学校
    *
    * @param whereParams 查询条件
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(SchoolQuery[] whereParams) throws ServiceException;

    /**
    * 根据查询条件，统计学校的数量
    *
    * @param whereParams 查询条件
    * @return 统计结果
    * @throws ServiceException 业务异常
    */
    int count(SchoolQuery whereParams) throws ServiceException;

    /**
    * 根据编号查询学校对象
    *
    * @param id 编号
    * @return 学校对象
    * @throws ServiceException 业务异常
    */
    SchoolPO get(Long id) throws ServiceException;

    /**
    * 根据条件查询学校
    *
    * @param whereParams 查询条件
    * @return 学校对象
    * @throws ServiceException 业务异常
    */
    SchoolPO get(SchoolQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询学校
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 学校对象
    * @throws ServiceException 业务异常
    */
    SchoolPO get(SchoolQuery whereParams, String[] selectFields) throws ServiceException;

    /**
    * 根据条件查询学校列表
    *
    * @param whereParams 查询条件
    * @return 学校对象列表
    * @throws ServiceException 业务异常
    */
    List<SchoolPO> list(SchoolQuery whereParams) throws ServiceException;

    /**
    * 根据条件查询学校列表
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return 学校对象列表
    * @throws ServiceException 业务异常
    */
    List<SchoolPO> list(SchoolQuery whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询学校列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return 学校对象列表
     * @throws ServiceException 业务异常
     */
     List<SchoolPO> listPaging(SchoolQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询学校列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @return 封装了分页的学校对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<SchoolPO> pages(SchoolQuery whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询学校列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @param selectFields 筛查出来的字段
    * @return 封装了分页的学校对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<SchoolPO> pages(SchoolQuery whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;

    /**
     * 进度查询
     *
     * @param param 查询条件
     * @param pageParam 分页参数
     * @return 进度列表
     * @throws ServiceException 业务异常
     * */
    PageRecords<SchoolVO> progressPages(SchoolQuery param, PageVO pageParam) throws ServiceException;

    /**
     * 学校任务完成数量进度
     *
     * @param param 查询参数
     * @return 进度
     * @throws ServiceException 业务异常
     */
    HartsResult overallProgress(SchoolQuery param) throws ServiceException;
}