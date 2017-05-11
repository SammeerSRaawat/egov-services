package org.egov.pgrrest.common.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "attribute_definition")
public class ValueDefinition extends AbstractPersistable<ValueDefinitionKey> {
    @EmbeddedId
    private ValueDefinitionKey id;

    @Column(name = "servicecode")
    private String serviceCode;

    @Column(name = "name")
    private String name;

    public String getAttributeCode() {
        return id.getAttributeCode();
    }

    public org.egov.pgrrest.common.model.ValueDefinition toDomain() {
        return new org.egov.pgrrest.common.model.ValueDefinition(name, id.getKey());
    }
}