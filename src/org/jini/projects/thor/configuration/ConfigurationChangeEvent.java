/*
   Copyright 2006 thor.jini.org Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

*/


package org.jini.projects.thor.configuration;

import java.rmi.MarshalledObject;

import org.jini.projects.thor.service.ChangeConstants;

/**
 * Remote event created by the instance when a change to <code>ConfigurationComponentHandler</code> 
 * or <code>ConfigurationFileHandler</code> occurs.
 */
public class ConfigurationChangeEvent extends net.jini.core.event.RemoteEvent implements ChangeConstants {
    
        static final long serialVersionUID = -1454416271189190003L;

        private int changeType = 0;
        public final static int ID = 3001;
      
        private Object reconstitutedConfig;
        private String component;
        private String entry;
        private String newValue;

        /** Creates a new instance of ChangeEvent, initialising all the data required to send back to the client */
        public ConfigurationChangeEvent(Object source, int changeType, long seqno, MarshalledObject key, String component, String entry, String newValue, String reconstitutedConfig) {
            super(source, ID, seqno, key);
            this.changeType = changeType;
            this.component = component;
            this.entry  =entry;
            this.newValue = newValue;
            this.reconstitutedConfig = reconstitutedConfig;
        }


        /**
         * Allows the client to obtain information about the change which occured.
         */
        public int getChangeType() {
            return changeType;
        }

        /**
         * Returns the current reconstitutedConfig as held in Thor.
         * if the item in question is a branch addition the reconstitutedConfig will be the "<<BRANCH>>"
         *if the change is a DELETE event, the reconstitutedConfig will be null.
         */
        public Object getReconstitutedConfig() {
            return reconstitutedConfig;
        }

   

        /**
         * @return Returns the component which contains the changed item.
         */
        public String getComponent() {
            return component;
        }
        /**
         * @param component The component to set.
         */
        public void setComponent(String component) {
            this.component = component;
        }
        /**
         * @return Returns the name of the entry which has been changed.
         */
        public String getEntry() {
            return entry;
        }
        /**
         * @param entry The entry to set.
         */
        public void setEntry(String entry) {
            this.entry = entry;
        }
        /**
         * @return Returns the new value of the entry.
         */
        public String getNewValue() {
            return newValue;
        }
        /**
         * @param newValue The new value to set.
         */
        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }
}
