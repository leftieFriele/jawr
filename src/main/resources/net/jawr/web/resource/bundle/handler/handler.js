if(!window.JAWR) 
	JAWR = {};
JAWR.loader = {
	usedBundles : {},
	script : function(path) {
		this.insert(this.jsbundles,'insertScript',this.mapping,path);
	},
	style: function(path,media) {
		this.insert(this.cssbundles,'insertCSS',this.cssmapping,path,media);
	},
	insert : function(bundles,func,mappingToUse,path,media){
		for(var x = 0; x < bundles.length;x++){
				var bundle = bundles[x];
				if(bundle.belongsToBundle(path) && !this.usedBundles[bundle.name]){
					this.usedBundles[bundle.name] = true;
					var pathtoRender = bundle.alternateProductionURL ? bundle.alternateProductionURL : this.normalizePath(mappingToUse +'/'+ bundle.prefix + bundle.name);
					if(bundle.ieExpression)
						this.insertCondComment(bundle.ieExpression,func,pathtoRender,media);
					else this[func](pathtoRender,media);					
				}
			}			
	},
	insertScript : function(path){
		document.write(' <script type="text/javascript" src="'+path+'" > </script> ');
	},
	insertCondComment : function(condition,func,path,media){
	 document.write('<!--[' + condition + ']>\n');
	 this[func](path,media);
	 document.write('<![endif]-->');
	},
	normalizePath : function(path) {
		while(path.indexOf('//')!=-1)
			path = path.replace('//','/');
		if(path.indexOf("http") == 0)
			path = path.replace(/http:\/(\w)/g,"http://$1").replace(/https:\/(\w)/g,"https://$1");
		return path;
	},
	insertCSS : function(path,media){
		media = media ? media : 'screen';
		document.write(' <link rel="stylesheet" type="text/css" media="' + media + '" href="'+path+'" /> ');		
	} 	
}
JAWR.ResourceBundle = function(name, prefix, itemPathList,ieExpression, alternateProductionURL){this.name = name;this.prefix = prefix;this.itemPathList = itemPathList;this.ieExpression=ieExpression;this.alternateProductionURL=alternateProductionURL;}
JAWR.ResourceBundle.prototype.belongsToBundle = function(path) {	
	if(path == this.name)
		return true;
	if(!this.itemPathList)
		return false;
	for(var x = 0; x < this.itemPathList.length; x++) {
		if(this.itemPathList[x] == path)
			return true;
	}
	return false;
}
