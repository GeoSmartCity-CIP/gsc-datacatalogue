package it.sinergis.datacatalogue.services.datastore;


public class PropertyLayer {
    
	
	private String logicalName = null; 
	private String physicalName = null; 
	private String physicalPath = null;
	private String title = null;
	private String srs = null;
	private String defaultStyle = null;
	private double[] bounds = null;
	
	/**
     * Costruttore di default.
     */
    public PropertyLayer () {
    }
    
    public PropertyLayer (String logicalName,String physicalName,String physicalPath,String title,String srs,String defaultStyle) {
    	this.logicalName = logicalName;
    	this.physicalName = physicalName;
    	this.physicalPath = physicalPath;
    	this.title = title;
    	this.srs = srs;
    	this.defaultStyle = defaultStyle;
    }
    
    public PropertyLayer (String logicalName,String physicalName,String physicalPath,String title,String srs,String defaultStyle,double[] bounds) {
    	this.logicalName = logicalName;
    	this.physicalName = physicalName;
    	this.physicalPath = physicalPath;
    	this.title = title;
    	this.srs = srs;
    	this.defaultStyle = defaultStyle;
    	this.bounds = bounds;
    }

    /**
     * Ritorna logicalName.
     * 
     * @return String logicalName
     */
    public String getLogicalName() {
        return this.logicalName;
    }

    /**
     * Imposta logicalName.
     * 
     * @param logicalName
     *           
     */
    public void setLogicalName(String logicalName) {
        this.logicalName = logicalName;
    }
    
    /**
     * Ritorna physicalName.
     * 
     * @return String physicalName
     */
    public String getPhysicalName() {
        return this.physicalName;
    }

    /**
     * Imposta physicalName.
     * 
     * @param physicalName
     *           
     */
    public void setPhysicalName(String physicalName) {
        this.physicalName = physicalName;
    }

    /**
     * Ritorna physicalPath.
     * 
     * @return String physicalPath
     */
    public String getPhysicalPath() {
        return this.physicalPath;
    }

    /**
     * Imposta physicalPath.
     * 
     * @param physicalPath
     *           
     */
    public void setPhysicalPath(String physicalPath) {
        this.physicalPath = physicalPath;
    }

    /**
     * Ritorna title.
     * 
     * @return String title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Imposta title.
     * 
     * @param title
     *           
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Ritorna srs.
     * 
     * @return String logicalName
     */
    public String getSrs() {
        return this.srs;
    }

    /**
     * Imposta srs.
     * 
     * @param srs
     *           
     */
    public void seSrs(String srs) {
        this.srs = srs;
    }

    /**
     * Ritorna defaultStyle.
     * 
     * @return String defaultStyle
     */
    public String getDefaultStyle() {
        return this.defaultStyle;
    }

    /**
     * Imposta defaultStyle.
     * 
     * @param defaultStyle
     *           
     */
    public void setDefaultStyle(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }



    /**
     * Ritorna bounds.
     * 
     * @return double[] bounds
     */
    public double[] getBounds() {
        return this.bounds;
    }

    /**
     * Imposta bounds.
     * 
     * @param bounds
     *           
     */
    public void setBounds(double[] bounds) {
        this.bounds = bounds;
    } 

}
