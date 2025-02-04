package cc.uncarbon.framework.crud.service;

import cc.uncarbon.framework.crud.entity.HelioBaseEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 服务基础模板
 * E = ENTITY 实体类
 *
 * @author Uncarbon
 */
public interface HelioBaseService<E extends HelioBaseEntity<?>>
        extends IService<E> {

}
