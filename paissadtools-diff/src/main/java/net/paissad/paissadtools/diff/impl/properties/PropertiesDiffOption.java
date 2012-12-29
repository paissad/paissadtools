package net.paissad.paissadtools.diff.impl.properties;

import lombok.Getter;
import lombok.Setter;
import net.paissad.paissadtools.diff.impl.AbstractFileDiffOption;

/**
 * The oriented option to use when using properties diff tool {@link PropertiesDiffTool}.
 * 
 * @author paissad
 */
public class PropertiesDiffOption extends AbstractFileDiffOption<PropertiesDiffExclusionRule> {

    @Getter
    @Setter
    private boolean skipValues;
}
