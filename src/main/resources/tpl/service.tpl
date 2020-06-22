package ${packageName}.service;

import ${packageName}.domain.${moduleName}VO;
import ${packageName}.domain.${moduleName}PO;
import ${packageName}.domain.${moduleName}ParamVO;
import com.dongpinyun.domain.PageRecords;
import com.dongpinyun.domain.PageVO;
import com.dongpinyun.utils.exception.ServiceException;

import java.util.List;

/**
* ${moduleCName}的业务层接口
* @author ${author}
* @date ${createDate}
*/
public interface ${moduleName}Service {

    /**
    * 新增${moduleCName}
    *
    * @param insertParams 待新增的${moduleCName}实例
    * @return 装填了自增型主键的${moduleCName}对象
    * @throws ServiceException 业务异常
    */
    ${moduleName}PO insert(${moduleName}PO insertParams) throws ServiceException;

    /**
    * 根据ID修改，返回修改结果
    *
    * @param setParams 待修改的${moduleCName}实例
    * @param ${columnList[0].name} 编号
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(${moduleName}PO setParams, ${columnList[0].type} ${columnList[0].name}) throws ServiceException;

    /**
    * 修改${moduleCName}，返回修改结果
    *
    * @param setParams 待修改的${moduleCName}实例
    * @param whereParams 查询条件
    * @return 修改结果
    * @throws ServiceException 业务异常
    */
    Boolean update(${moduleName}PO setParams, ${moduleName}Query whereParams) throws ServiceException;

    /**
    * 修改${moduleCName}，返回受影响的行数
    *
    * @param setParams 待修改的${moduleCName}实例
    * @param whereParams 查询条件
    * @return 受影响的行数
    * @throws ServiceException 业务异常
    */
    Integer updateAndRtnRows(${moduleName}PO setParams, ${moduleName}Query whereParams) throws ServiceException;

    /**
    * 根据ID删除${moduleCName}
    *
    * @param ${columnList[0].name} 编号
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(${columnList[0].type} ${columnList[0].name}) throws ServiceException;

    /**
    * 根据ID数组，批量删除${moduleCName}
    *
    * @param ${columnList[0].name} 编号数组
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(${columnList[0].type}[] ${columnList[0].name}) throws ServiceException;

    /**
    * 根据查询条件，删除${moduleCName}
    *
    * @param whereParams 查询条件
    * @return 删除结果
    * @throws ServiceException 业务异常
    */
    Boolean delete(${moduleName}Query whereParams) throws ServiceException;

    /**
    * 根据查询条件数组，批量删除${moduleCName}
    *
    * @param whereParams 查询条件
    * @return 删除结果数组
    * @throws ServiceException 业务异常
    */
    Boolean[] delete(${moduleName}Query[] whereParams) throws ServiceException;

    /**
    * 根据查询条件，统计${moduleCName}的数量
    *
    * @param whereParams 查询条件
    * @return 统计结果
    * @throws ServiceException 业务异常
    */
    int count(${moduleName}Query whereParams) throws ServiceException;

    /**
    * 根据编号查询${moduleCName}对象
    *
    * @param ${columnList[0].name} 编号
    * @return ${moduleCName}对象
    * @throws ServiceException 业务异常
    */
    ${moduleName}PO get(${columnList[0].type} ${columnList[0].name}) throws ServiceException;

    /**
    * 根据条件查询${moduleCName}
    *
    * @param whereParams 查询条件
    * @return ${moduleCName}对象
    * @throws ServiceException 业务异常
    */
    ${moduleName}PO get(${moduleName}Query whereParams) throws ServiceException;

    /**
    * 根据条件查询${moduleCName}
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return ${moduleCName}对象
    * @throws ServiceException 业务异常
    */
    ${moduleName}PO get(${moduleName}Query whereParams, String[] selectFields) throws ServiceException;

    /**
    * 根据条件查询${moduleCName}列表
    *
    * @param whereParams 查询条件
    * @return ${moduleCName}对象列表
    * @throws ServiceException 业务异常
    */
    List<${moduleName}PO> list(${moduleName}Query whereParams) throws ServiceException;

    /**
    * 根据条件查询${moduleCName}列表
    *
    * @param whereParams 查询条件
    * @param selectFields 筛查出来的字段
    * @return ${moduleCName}对象列表
    * @throws ServiceException 业务异常
    */
    List<${moduleName}PO> list(${moduleName}Query whereParams, String[] selectFields) throws ServiceException;

    /**
     * 根据条件查询${moduleCName}列表
     *
     * @param whereParams 查询条件
     * @param pageParams 分页实例
     * @return ${moduleCName}对象列表
     * @throws ServiceException 业务异常
     */
     List<${moduleName}PO> listPaging(${moduleName}Query whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询${moduleCName}列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @return 封装了分页的${moduleCName}对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<${moduleName}PO> pages(${moduleName}Query whereParams, PageVO pageParams) throws ServiceException;

    /**
    * 根据条件分页查询${moduleCName}列表
    *
    * @param whereParams 查询条件
    * @param pageParams 分页实例
    * @param selectFields 筛查出来的字段
    * @return 封装了分页的${moduleCName}对象列表
    * @throws ServiceException 业务异常
    */
    PageRecords<${moduleName}PO> pages(${moduleName}Query whereParams, PageVO pageParams, String[] selectFields) throws ServiceException;
}