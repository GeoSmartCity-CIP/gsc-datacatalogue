/*
 * Created on 18 dic 2015 ( Time 16:29:07 )
 * Generated by Telosys Tools Generator ( version 2.1.1 )
 */
// This Bean has a basic Primary Key (not composite) 

package it.sinergis.datacatalogue.bean.jpa;

import java.io.Serializable;

//import javax.validation.constraints.* ;
//import org.hibernate.validator.constraints.* ;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernatespatial.postgis.StringJsonUserType;

/**
 * Persistent class for entity stored in table "gsc_007_dataset"
 *
 * @author Telosys Tools Generator
 *
 */

@Entity
@Table(name="gsc_007_dataset", schema="gscdatacatalogue" )
@TypeDefs({ @TypeDef(name = "StringJsonObject", typeClass = StringJsonUserType.class) })
// Define named queries here
@NamedQueries ( {
  @NamedQuery ( name="Gsc007DatasetEntity.countAll", query="SELECT COUNT(x) FROM Gsc007DatasetEntity x" )
} )
public class Gsc007DatasetEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //----------------------------------------------------------------------
    // ENTITY PRIMARY KEY ( BASED ON A SINGLE FIELD )
    //----------------------------------------------------------------------
    @Id
    @Column(name="id", nullable=false)
    @SequenceGenerator(name = "gsc_007_dataset_id_seq", sequenceName = "gsc_007_dataset_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gsc_007_dataset_id_seq")
    private Long       id           ;


    //----------------------------------------------------------------------
    // ENTITY DATA FIELDS 
    //----------------------------------------------------------------------    
    @Type(type = "StringJsonObject")    
    @Column(name="json", nullable=false)
    private String     json         ;



    //----------------------------------------------------------------------
    // ENTITY LINKS ( RELATIONSHIP )
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // CONSTRUCTOR(S)
    //----------------------------------------------------------------------
    public Gsc007DatasetEntity() {
		super();
    }
    
    //----------------------------------------------------------------------
    // GETTER & SETTER FOR THE KEY FIELD
    //----------------------------------------------------------------------
    public void setId( Long id ) {
        this.id = id ;
    }
    public Long getId() {
        return this.id;
    }

    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR FIELDS
    //----------------------------------------------------------------------
    //--- DATABASE MAPPING : json ( json ) 
    public void setJson( String json ) {
        this.json = json;
    }
    public String getJson() {
        return this.json;
    }


    //----------------------------------------------------------------------
    // GETTERS & SETTERS FOR LINKS
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    // toString METHOD
    //----------------------------------------------------------------------
    public String toString() { 
        StringBuffer sb = new StringBuffer(); 
        sb.append("["); 
        sb.append(id);
        sb.append("]:"); 
        sb.append(json);
        return sb.toString(); 
    } 

}
