/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.siddhi.editor.core.util.designview.codegenerator.generators;

import org.wso2.carbon.siddhi.editor.core.util.designview.beans.configs.siddhielements.TriggerConfig;
import org.wso2.carbon.siddhi.editor.core.util.designview.constants.SiddhiCodeBuilderConstants;
import org.wso2.carbon.siddhi.editor.core.util.designview.exceptions.CodeGenerationException;
import org.wso2.carbon.siddhi.editor.core.util.designview.utilities.CodeGeneratorUtils;

/**
 * Generates the code for a Siddhi trigger element
 */
public class TriggerCodeGenerator {

    /**
     * Generates the Siddhi code representation of a TriggerConfig object
     *
     * @param trigger The TriggerConfig object
     * @return The Siddhi code representation of the given TriggerConfig object
     * @throws CodeGenerationException Error when generating the code
     */
    public String generateTrigger(TriggerConfig trigger, boolean isToolTip) throws CodeGenerationException {
        CodeGeneratorUtils.NullValidator.validateConfigObject(trigger);
        StringBuilder triggerStringBuilder = new StringBuilder();
        if (!isToolTip) {
            triggerStringBuilder.append(SubElementCodeGenerator.generateComment(trigger.getPreviousCommentSegment()));
        }

        triggerStringBuilder.append(SubElementCodeGenerator.generateAnnotations(trigger.getAnnotationList()))
                .append(SiddhiCodeBuilderConstants.DEFINE_TRIGGER)
                .append(SiddhiCodeBuilderConstants.SPACE)
                .append(trigger.getName())
                .append(SiddhiCodeBuilderConstants.SPACE)
                .append(SiddhiCodeBuilderConstants.AT)
                .append(SiddhiCodeBuilderConstants.SPACE)
                .append(trigger.getAt())
                .append(SiddhiCodeBuilderConstants.SEMI_COLON);

        return  triggerStringBuilder.toString();
    }

}
