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
**/
package com.obs.services.model;

import java.util.ArrayList;
import java.util.List;

import com.obs.services.internal.ObsConvertor;


public class AbstractNotification extends HeaderResponse
{
    
    public AbstractNotification(){
        
    }
    

    public AbstractNotification(String id, Filter filter, List<EventTypeEnum> events)
    {
        this.id = id;
        this.filter = filter;
        this.events = events;
    }

    protected String id;
    
    protected Filter filter;
    
    protected List<EventTypeEnum> events;
    

    public static class Filter{
        
        private List<FilterRule> filterRules;
        
 
        public static class FilterRule{
            
            private String name;
            
            private String value;

            public FilterRule(){
                
            }
            
    
            public FilterRule(String name, String value){
                this.name = name;
                this.value = value;
            }
            
   
            public String getName()
            {
                return name;
            }

 
            public void setName(String name)
            {
                this.name = name;
            }

    
            public String getValue()
            {
                return value;
            }


            public void setValue(String value)
            {
                this.value = value;
            }

            @Override
            public int hashCode()
            {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((name == null) ? 0 : name.hashCode());
                result = prime * result + ((value == null) ? 0 : value.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj)
            {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                FilterRule other = (FilterRule)obj;
                if (name == null)
                {
                    if (other.name != null)
                        return false;
                }
                else if (!name.equals(other.name))
                    return false;
                if (value == null)
                {
                    if (other.value != null)
                        return false;
                }
                else if (!value.equals(other.value))
                    return false;
                return true;
            }

            @Override
            public String toString()
            {
                return "FilterRule [name=" + name + ", value=" + value + "]";
            }
            
        }
        

        public List<FilterRule> getFilterRules()
        {
            if(this.filterRules == null){
                this.filterRules = new ArrayList<FilterRule>();
            }
            return filterRules;
        }


        public void setFilterRules(List<FilterRule> filterRules)
        {
            this.filterRules = filterRules;
        }
        

        public void addFilterRule(String name, String value){
            this.getFilterRules().add(new FilterRule(name, value));
        }

        @Override
        public String toString()
        {
            return "Filter [fileterRules=" + filterRules + "]";
        }
        
    }


    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }

   
    @Deprecated
    public List<String> getEvents()
    {
    	List<String> list = new ArrayList<String>();
    	for(EventTypeEnum e : this.getEventTypes()) {
    		list.add(ObsConvertor.transEventTypeStatic(e));
    	}
        return list;
    }

    @Deprecated
    public void setEvents(List<String> events)
    {
    	if(events != null) {
    		for(String event : events) {
    			EventTypeEnum e = EventTypeEnum.getValueFromCode(event);
    			if(e != null) {
    				this.getEventTypes().add(e);
    			}
    		}
    	}
    }


    public List<EventTypeEnum> getEventTypes()
    {
        if(this.events == null){
            this.events = new ArrayList<EventTypeEnum>();
        }
        return events;
    }


    public void setEventTypes(List<EventTypeEnum> events)
    {
    	this.events = events;
    }


    public Filter getFilter()
    {
        return filter;
    }


    public void setFilter(Filter filter)
    {
        this.filter = filter;
    }
    
}
