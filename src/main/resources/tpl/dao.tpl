package ${packageName}.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.dongpinyun.domain.PageVO;
import ${packageName}.domain.${moduleName}VO;
import ${packageName}.domain.${moduleName}PO;

/**
* ${moduleCName}的持久层接口类
* @author ${author}
* @date ${createDate}
*/
public interface ${moduleName}DAO {

    /**
    * 新增${moduleCName}
    * @param insertParams 待新增的${moduleCName}实例
    * @return 自增型主键
    */
    int insert(@Param("insertParams") ${moduleName}PO insertParams);

    /**
    * 修改${moduleCName}
    * @param setParams 待修改的${moduleCName}实例
    * @param whereParams 查询条件
    * @return 修改结果
    */
    int update(@Param("setParams") ${moduleName}PO setParams, @Param("whereParams") ${moduleName}Query whereParams);

    /**
    * 根据查询条件，删除${moduleCName}
    * @param whereParams 查询条件
    * @return 删除结果
    */
    int delete(@Param("whereParams") ${moduleName}Query whereParams);

    /**
    * 根据查询条件，统计${moduleCName}的数量
    * @param whereParams 查询条件
    * @return 统计结果
    */
    int count(@Param("whereParams") ${moduleName}Query whereParams);

    /**
    * 根据条件查询${moduleCName}
    * @param whereParams 查询条件
    * @return ${moduleCName}对象
    */
    ${moduleName}PO get(@Param("whereParams") ${moduleName}Query whereParams);

    /**
    * 根据条件查询${moduleCName}
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return ${moduleCName}对象
    */
    ${moduleName}PO select(@Param("whereParams") ${moduleName}Query whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件查询${moduleCName}列表
    * @param whereParams 查询条件
    * @return ${moduleCName}对象
    */
    List<${moduleName}PO> list(@Param("whereParams") ${moduleName}Query whereParams);

    /**
    * 根据条件查询${moduleCName}列表
    * @param whereParams 查询条件
    * @param selectFields 筛选出来的字段
    * @return ${moduleCName}对象
    */
    List<${moduleName}PO> selects(@Param("whereParams") ${moduleName}Query whereParams, @Param("selectFields") String[] selectFields);

    /**
    * 根据条件分页查询${moduleCName}列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @return ${moduleCName}对象列表
    */
    List<${moduleName}PO> listPaging(@Param("whereParams") ${moduleName}Query whereParams, @Param("pageParams") PageVO pageParams);

    /**
    * 根据条件分页查询${moduleCName}列表
    * @param whereParams 查询条件
    * @param pageParams 分页对象
    * @param selectFields 筛选出来的字段
    * @return ${moduleCName}对象列表
    */
    List<${moduleName}PO> selectsPaging(@Param("whereParams") ${moduleName}Query whereParams, @Param("pageParams") PageVO pageParams, @Param("selectFields") String[] selectFields);
}