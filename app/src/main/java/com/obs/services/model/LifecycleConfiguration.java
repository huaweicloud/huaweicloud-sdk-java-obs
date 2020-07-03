/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.obs.services.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.obs.services.internal.utils.ServiceUtils;

/**
 * 桶的生命周期配置
 */
public class LifecycleConfiguration extends HeaderResponse {
    private List<Rule> rules;

    /**
     * 构造方法
     * 
     * @param rules
     *            桶的生命周期规则列表
     */
    public LifecycleConfiguration(List<Rule> rules) {
        this.rules = rules;
    }

    public LifecycleConfiguration() {
    }

    /**
     * 获取桶的生命周期规则列表
     * 
     * @return 生命周期规则列表
     */
    public List<Rule> getRules() {
        if (this.rules == null) {
            this.rules = new ArrayList<Rule>();
        }
        return rules;
    }

    /**
     * 新增生命周期规则
     * 
     * @param rule
     *            生命周期规则
     */
    public void addRule(Rule rule) {
        if (!getRules().contains(rule)) {
            getRules().add(rule);
        }
    }

    /**
     * 创建并新增一条生命周期的规则
     * 
     * @param id
     *            规则ID号
     * @param prefix
     *            对象名前缀，用以标识哪些对象可以匹配当前规则
     * @param enabled
     *            规则是否启用标识
     * @return rule 生命周期规则
     */
    public Rule newRule(String id, String prefix, Boolean enabled) {
        Rule rule = this.new Rule(id, prefix, enabled);
        getRules().add(rule);
        return rule;
    }

    public static void setDays(TimeEvent timeEvent, Integer days) {
        if (timeEvent != null) {
            timeEvent.days = days;
        }
    }

    public static void setDate(TimeEvent timeEvent, Date date) {
        if (timeEvent != null) {
            timeEvent.date = date;
        }
    }

    public static void setStorageClass(TimeEvent timeEvent, StorageClassEnum storageClass) {
        if (timeEvent != null) {
            timeEvent.storageClass = storageClass;
        }
    }

    public abstract class TimeEvent {
        protected Integer days;

        protected Date date;

        protected StorageClassEnum storageClass;

        public TimeEvent() {
        }

        protected TimeEvent(Integer days) {
            this.days = days;
        }

        protected TimeEvent(Date date) {
            this.date = date;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((date == null) ? 0 : date.hashCode());
            result = prime * result + ((days == null) ? 0 : days.hashCode());
            result = prime * result + ((storageClass == null) ? 0 : storageClass.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TimeEvent other = (TimeEvent) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (date == null) {
                if (other.date != null) {
                    return false;
                }
            } else if (!date.equals(other.date)) {
                return false;
            }
            if (days == null) {
                if (other.days != null) {
                    return false;
                }
            } else if (!days.equals(other.days)) {
                return false;
            }
            if (storageClass != other.storageClass) {
                return false;
            }
            return true;
        }

        private LifecycleConfiguration getOuterType() {
            return LifecycleConfiguration.this;
        }

    }

    /**
     * 历史版本对象过期时间配置
     *
     */
    public class NoncurrentVersionExpiration extends TimeEvent {

        public NoncurrentVersionExpiration() {
        }

        /**
         * 构造函数
         * 
         * @param days
         *            历史版本对象过期时间，表示对象在成为历史版本之后第几天时过期
         */
        public NoncurrentVersionExpiration(Integer days) {
            this.setDays(days);
        }

        /**
         * 获取历史版本对象过期时间
         * 
         * @return 历史版本对象过期时间，表示对象在成为历史版本之后第几天时过期
         */
        public Integer getDays() {
            return days;
        }

        /**
         * 设置历史版本对象过期时间
         * 
         * @param days
         *            历史版本对象过期时间，表示对象在成为历史版本之后第几天时过期
         */
        public void setDays(Integer days) {
            this.days = days;
        }

        @Override
        public String toString() {
            return "NoncurrentVersionExpiration [days=" + days + "]";
        }

    }

    /**
     * 对象过期时间配置
     */
    public class Expiration extends TimeEvent {

        public Expiration() {
        }

        /**
         * 构造函数
         * 
         * @param date
         *            对象过期日期， 表示对象过期的具体日期
         */
        public Expiration(Date date) {
            super(date);
        }

        /**
         * 构造函数
         * 
         * @param days
         *            对象过期时间，表示在对象创建时间后第几天时过期
         */
        public Expiration(Integer days) {
            super(days);
        }

        /**
         * 获取对象过期时间
         * 
         * @return 对象过期时间，表示在对象创建时间后第几天时过期
         */
        public Integer getDays() {
            return days;
        }

        /**
         * 设置对象过期时间
         * 
         * @param days
         *            对象过期时间，表示在对象创建时间后第几天时过期
         */
        public void setDays(Integer days) {
            this.days = days;
            this.date = null;
        }

        /**
         * 获取对象过期日期
         * 
         * @return 对象过期日期， 表示对象过期的具体日期
         */
        public Date getDate() {
            return ServiceUtils.cloneDateIgnoreNull(this.date);
        }

        /**
         * 获取对象过期日期
         * 
         * @param date
         *            对象过期日期， 表示对象过期的具体日期
         */
        public void setDate(Date date) {
            this.date = ServiceUtils.cloneDateIgnoreNull(date);
            this.days = null;
        }

        @Override
        public String toString() {
            return "Expiration [days=" + days + ", date=" + date + "]";
        }

    }

    /**
     * 对象转换策略
     *
     */
    public class Transition extends TimeEvent {

        public Transition() {
            super();
        }

        /**
         * 构造函数
         * 
         * @param date
         *            对象转换日期， 表示对象转换的具体日期
         * @param storageClass
         *            对象转换后的存储类别，支持WARM或COLD
         */
        @Deprecated
        public Transition(Date date, String storageClass) {
            super(date);
            this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
        }

        /**
         * 构造函数
         * 
         * @param date
         *            对象转换日期， 表示对象转换的具体日期
         * @param storageClass
         *            对象转换后的存储类别，支持WARM或COLD
         */
        public Transition(Date date, StorageClassEnum storageClass) {
            super(date);
            this.storageClass = storageClass;
        }

        /**
         * 构造函数
         * 
         * @param days
         *            对象转换时间，表示在对象创建时间后第几天时自动转换
         * @param storageClass
         *            对象转换后的存储类别，支持WARM或COLD
         */
        @Deprecated
        public Transition(Integer days, String storageClass) {
            super(days);
            this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
        }

        /**
         * 构造函数
         * 
         * @param days
         *            对象转换时间，表示在对象创建时间后第几天时自动转换
         * @param storageClass
         *            对象转换后的存储类别，支持WARM或COLD
         */
        public Transition(Integer days, StorageClassEnum storageClass) {
            super(days);
            this.storageClass = storageClass;
        }

        /**
         * 获取对象转换后的存储类别
         * 
         * @return 对象转换后的存储类别
         * @see #getObjectStorageClass()
         */
        @Deprecated
        public String getStorageClass() {
            return storageClass != null ? this.storageClass.getCode() : null;
        }

        /**
         * 设置对象转换后的存储类别
         * 
         * @param storageClass
         *            对象转换后的存储类别
         * @see #setObjectStorageClass(StorageClassEnum storageClass)
         */
        @Deprecated
        public void setStorageClass(String storageClass) {
            this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
        }

        /**
         * 获取对象转换后的存储类别
         * 
         * @return 对象转换后的存储类别
         */
        public StorageClassEnum getObjectStorageClass() {
            return storageClass;
        }

        /**
         * 设置对象转换后的存储类别
         * 
         * @param storageClass
         *            对象转换后的存储类别
         */
        public void setObjectStorageClass(StorageClassEnum storageClass) {
            this.storageClass = storageClass;
        }

        /**
         * 获取对象转换时间
         * 
         * @return 对象转换时间，表示在对象创建时间后第几天时自动转换
         */
        public Integer getDays() {
            return days;
        }

        /**
         * 设置对象转换时间
         * 
         * @param days
         *            对象转换时间，表示在对象创建时间后第几天时自动转换
         */
        public void setDays(Integer days) {
            this.days = days;
            this.date = null;
        }

        /**
         * 获取对象转换日期
         * 
         * @return 对象转换日期， 表示对象转换的具体日期
         */
        public Date getDate() {
            return ServiceUtils.cloneDateIgnoreNull(this.date);
        }

        /**
         * 设置对象转换日期
         * 
         * @param date
         *            对象转换日期， 表示对象转换的具体日期
         */
        public void setDate(Date date) {
            this.date = ServiceUtils.cloneDateIgnoreNull(date);
            this.days = null;
        }

        @Override
        public String toString() {
            return "Transition [days=" + days + ", date=" + date + ", storageClass=" + storageClass + "]";
        }
    }

    /**
     * 历史版本对象转换策略
     *
     */
    public class NoncurrentVersionTransition extends TimeEvent {
        public NoncurrentVersionTransition() {
        }

        /**
         * 构造函数
         * 
         * @param days
         *            历史版本对象转换时间，表示对象在成为历史版本之后第几天时自动转换
         * @param storageClass
         *            历史版本对象转换后的存储类别
         */
        @Deprecated
        public NoncurrentVersionTransition(Integer days, String storageClass) {
            this.setDays(days);
            this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
        }

        /**
         * 构造函数
         * 
         * @param days
         *            历史版本对象转换时间，表示对象在成为历史版本之后第几天时自动转换
         * @param storageClass
         *            历史版本对象转换后的存储类别
         */
        public NoncurrentVersionTransition(Integer days, StorageClassEnum storageClass) {
            this.setDays(days);
            this.storageClass = storageClass;
        }

        /**
         * 获取历史版本对象转换时间
         * 
         * @return 历史版本对象转换时间，表示对象在成为历史版本之后第几天时自动转换
         */
        public Integer getDays() {
            return days;
        }

        /**
         * 设置历史版本对象转换时间
         * 
         * @param days
         *            历史版本对象转换时间，表示对象在成为历史版本之后第几天时自动转换
         */
        public void setDays(Integer days) {
            this.days = days;
        }

        /**
         * 获取历史版本对象转换后的存储类别
         * 
         * @return 历史版本对象转换后的存储类别
         * @see #getObjectStorageClass()
         */
        @Deprecated
        public String getStorageClass() {
            return storageClass != null ? this.storageClass.getCode() : null;
        }

        /**
         * 设置历史版本对象转换后的存储类别
         * 
         * @param storageClass
         *            历史版本对象转换后的存储类别
         * @see #setObjectStorageClass(StorageClassEnum storageClass)
         */
        @Deprecated
        public void setStorageClass(String storageClass) {
            this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
        }

        /**
         * 获取历史版本对象转换后的存储类别
         * 
         * @return 历史版本对象转换后的存储类别
         */
        public StorageClassEnum getObjectStorageClass() {
            return storageClass;
        }

        /**
         * 设置历史版本对象转换后的存储类别
         * 
         * @param storageClass
         *            历史版本对象转换后的存储类别
         */
        public void setObjectStorageClass(StorageClassEnum storageClass) {
            this.storageClass = storageClass;
        }

        @Override
        public String toString() {
            return "NoncurrentVersionTransition [days=" + days + ", storageClass=" + storageClass + "]";
        }

    }

    /**
     * 桶的生命周期规则
     */
    public class Rule {
        protected String id;

        protected String prefix;

        protected Boolean enabled;

        protected Expiration expiration;

        protected NoncurrentVersionExpiration noncurrentVersionExpiration;

        protected List<Transition> transitions;

        protected List<NoncurrentVersionTransition> noncurrentVersionTransitions;

        /**
         * 无参构造方法
         */
        public Rule() {
        }

        /**
         * @param id
         *            规则ID号
         * @param prefix
         *            对象名前缀，用以标识哪些对象可以匹配当前规则
         * @param enabled
         *            规则是否启用标识
         */
        public Rule(String id, String prefix, Boolean enabled) {
            this.id = id;
            this.prefix = prefix;
            this.enabled = enabled;
        }

        /**
         * 创建对象过期时间配置
         * 
         * @return 过期时间配置实例
         */
        public Expiration newExpiration() {
            this.expiration = new Expiration();
            return this.expiration;
        }

        /**
         * 创建历史版本对象过期时间配置
         * 
         * @return 历史版本对象过期时间配置
         */
        public NoncurrentVersionExpiration newNoncurrentVersionExpiration() {
            this.noncurrentVersionExpiration = new NoncurrentVersionExpiration();
            return this.noncurrentVersionExpiration;
        }

        /**
         * 创建对象转换策略
         * 
         * @return 对象转换策略
         */
        public Transition newTransition() {
            if (this.transitions == null) {
                this.transitions = new ArrayList<Transition>();
            }
            Transition t = new Transition();
            this.transitions.add(t);
            return t;
        }

        /**
         * 创建历史版本对象转换策略
         * 
         * @return 历史版本对象转换策略
         */
        public NoncurrentVersionTransition newNoncurrentVersionTransition() {
            if (this.noncurrentVersionTransitions == null) {
                this.noncurrentVersionTransitions = new ArrayList<NoncurrentVersionTransition>();
            }
            NoncurrentVersionTransition nt = new NoncurrentVersionTransition();
            this.noncurrentVersionTransitions.add(nt);
            return nt;
        }

        /**
         * 获取规则ID号
         * 
         * @return 规则ID号
         */
        public String getId() {
            return id;
        }

        /**
         * 设置规则ID号
         * 
         * @param id
         *            规则ID号
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * 获取对象名前缀，用以标识哪些对象可以匹配当前规则
         * 
         * @return 对象名前缀
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * 设置对象名前缀，用以标识哪些对象可以匹配当前规则
         * 
         * @param prefix
         *            对象名前缀
         */
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        /**
         * 判断规则是否启用
         * 
         * @return 规则是否启用标识
         */
        public Boolean getEnabled() {
            return enabled;
        }

        /**
         * 设置规则是否启用
         * 
         * @param enabled
         *            规则是否启用标识
         */
        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * 获取对象过期时间配置
         * 
         * @return 对象过期时间配置
         */
        public Expiration getExpiration() {
            return expiration;
        }

        /**
         * 设置对象过期时间配置
         * 
         * @param expiration
         *            对象过期时间配置
         */
        public void setExpiration(Expiration expiration) {
            this.expiration = expiration;
        }

        /**
         * 获取历史版本对象过期时间配置
         * 
         * @return 历史版本对象过期时间配置
         */
        public NoncurrentVersionExpiration getNoncurrentVersionExpiration() {
            return noncurrentVersionExpiration;
        }

        /**
         * 设置历史版本对象过期时间配置
         * 
         * @param noncurrentVersionExpiration
         *            历史版本对象过期时间配置
         */
        public void setNoncurrentVersionExpiration(NoncurrentVersionExpiration noncurrentVersionExpiration) {
            this.noncurrentVersionExpiration = noncurrentVersionExpiration;
        }

        /**
         * 获取对象转换策略
         * 
         * @return 对象转换策略
         */
        public List<Transition> getTransitions() {
            if (this.transitions == null) {
                this.transitions = new ArrayList<Transition>();
            }
            return transitions;
        }

        /**
         * 设置对象转换策略
         * 
         * @param transitions
         *            对象转换策略
         */
        public void setTransitions(List<Transition> transitions) {
            this.transitions = transitions;
        }

        /**
         * 获取历史版本对象转换策略
         * 
         * @return 历史版本对象转换策略
         */
        public List<NoncurrentVersionTransition> getNoncurrentVersionTransitions() {
            if (this.noncurrentVersionTransitions == null) {
                this.noncurrentVersionTransitions = new ArrayList<NoncurrentVersionTransition>();
            }
            return noncurrentVersionTransitions;
        }

        /**
         * 设置历史版本对象转换策略
         * 
         * @param noncurrentVersionTransitions
         *            历史版本对象转换策略
         */
        public void setNoncurrentVersionTransitions(List<NoncurrentVersionTransition> noncurrentVersionTransitions) {
            this.noncurrentVersionTransitions = noncurrentVersionTransitions;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((enabled == null) ? 0 : enabled.hashCode());
            result = prime * result + ((expiration == null) ? 0 : expiration.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result
                    + ((noncurrentVersionExpiration == null) ? 0 : noncurrentVersionExpiration.hashCode());
            result = prime * result
                    + ((noncurrentVersionTransitions == null) ? 0 : noncurrentVersionTransitions.hashCode());
            result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
            result = prime * result + ((transitions == null) ? 0 : transitions.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Rule other = (Rule) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (enabled == null) {
                if (other.enabled != null) {
                    return false;
                }
            } else if (!enabled.equals(other.enabled)) {
                return false;
            }
            if (expiration == null) {
                if (other.expiration != null) {
                    return false;
                }
            } else if (!expiration.equals(other.expiration)) {
                return false;
            }
            if (id == null) {
                if (other.id != null) {
                    return false;
                }
            } else if (!id.equals(other.id)) {
                return false;
            }
            if (noncurrentVersionExpiration == null) {
                if (other.noncurrentVersionExpiration != null) {
                    return false;
                }
            } else if (!noncurrentVersionExpiration.equals(other.noncurrentVersionExpiration)) {
                return false;
            }
            if (noncurrentVersionTransitions == null) {
                if (other.noncurrentVersionTransitions != null) {
                    return false;
                }
            } else if (!noncurrentVersionTransitions.equals(other.noncurrentVersionTransitions)) {
                return false;
            }
            if (prefix == null) {
                if (other.prefix != null) {
                    return false;
                }
            } else if (!prefix.equals(other.prefix)) {
                return false;
            }
            if (transitions == null) {
                if (other.transitions != null) {
                    return false;
                }
            } else if (!transitions.equals(other.transitions)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Rule [id=" + id + ", prefix=" + prefix + ", enabled=" + enabled + ", expiration=" + expiration
                    + ", noncurrentVersionExpiration=" + noncurrentVersionExpiration + ", transitions=" + transitions
                    + ", noncurrentVersionTransitions=" + noncurrentVersionTransitions + "]";
        }

        private LifecycleConfiguration getOuterType() {
            return LifecycleConfiguration.this;
        }

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LifecycleConfiguration that = (LifecycleConfiguration) o;
        if (rules != null ? !rules.equals(that.rules) : that.rules != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return rules != null ? rules.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LifecycleConfiguration [rules=" + rules + "]";
    }

}
