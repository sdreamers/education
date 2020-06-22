package ${packageName}validation;

import ${packageName}.domain.${moduleName}PO;
import com.dongpinyun.utils.exception.ServiceException;

/**
* ${moduleCName}的入参校验类
* @author ${author}
* @date ${createDate}
*/
public class ${moduleName}Validation {

    /**
    * 校验“新增${moduleCName}”的入参
    *
    * @param insertParams 待新增的${moduleCName}实例
    * @throws ServiceException 业务异常
    */
    public static void validateInsert(${moduleName}PO insertParams) throws ServiceException {

    }

    /**
    * 校验“修改${moduleCName}”的入参
    *
    * @param setParams 待修改的${moduleCName}实例
    * @param id 编号
    * @throws ServiceException 业务异常
    */
    public static void validateUpdate(${moduleName}PO setParams, Integer id) throws ServiceException {

    }

    /**
    * 校验“删除${moduleCName}”的入参
    *
    * @param id 编号
    * @throws ServiceException 业务异常
    */
    public static void validateDelete(Integer id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("请输入${moduleCName}ID");
        }
    }

    /**
    * 校验“根据编号查询${moduleCName}”的入参
    *
    * @param id 编号
    * @throws ServiceException 业务异常
    */
    public static void validateGet(Integer id) throws ServiceException {
        if (id == null) {
            throw new ServiceException("请输入${moduleCName}ID");
        }
    }
}