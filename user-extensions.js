Selenium.prototype.doTypeRepeated = function(locator, text) {
    // All locator-strategies are automatically handled by "findElement"
    var element = this.page().findElement(locator);

    // Create the text to type
    var valueToType = text + text;

    // Replace the element text with the new text
    this.page().replaceText(element, valueToType);mou

};


Selenium.prototype.doStoreRandomString = function(variableName){
length= 10
var chars = 'ABCDEFGpiyushHImishraJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz'.split('');
if (! length) {
        length = Math.floor(Math.random() * chars.length);
    }
    
    var str = '';
    for (var i = 0; i < length; i++) {
        str += chars[Math.floor(Math.random() * chars.length)];
    }
   storedVars[variableName] = str;
};

Selenium.prototype.doStoreTimer = function(variableName){
    var dates = new Date()
    var start = dates.getTime();
    storedVars[variableName]= start;
};

Selenium.prototype.doStoreRandom3 = function(variableName){
  random = Math.floor(Math.random()*6);
  storedVars[variableName] = random;
};

Selenium.prototype.doStoreRandom = function(variableName){
  random = Math.floor(Math.random()*10000000);
  storedVars[variableName] = random;
};

Selenium.prototype.doTypeTodaysDate = function(locator){
  var dates = new Date();
  var day = dates.getDate();
  if (day < 10){
     day = '0' + day;
  }
  month = dates.getMonth() + 1;
  if (month < 10){
     month = '0' + month;
  }
  var year = dates.getFullYear();
  var prettyDay = month + '/' + day + '/' + year;
  this.doType(locator, prettyDay);
};

Selenium.prototype.doStoreTodaysDate = function(variableName){
  var dates = new Date();
  var day = dates.getDate();
  if (day < 10){
     day = day;
  }
  month = dates.getMonth() + 1;
  if (month < 10){
     month = month;
  }
  var year = dates.getFullYear();
  var prettyDay = month + '/' + day + '/' + year;
  storedVars[variableName]= prettyDay;
};

Selenium.prototype.doStoreTodaysDateWithoutSeprator = function(variableName){
 var dates = new Date();
 var day = dates.getDate();
 if (day < 10){
    day = '0' + day;
 }
 month = dates.getMonth() + 1;
 if (month < 10){
    month = '0' + month;
 }
 var year = dates.getFullYear();
 var prettyDay = month + day + year;
 storedVars[variableName]= prettyDay;
};

Selenium.prototype.doStoreTommorrowDate = function(variableName){
  var dates = new Date();
  var day = dates.getDate();
day=day+1;
  if (day < 10){
     day = day;
  }
  month = dates.getMonth() + 1;
  if (month < 10){
     month = month;
  }
  var year = dates.getFullYear();
  var prettyDay = month + '/' + day + '/' + year;
  storedVars[variableName]= prettyDay;
};

Selenium.prototype.doStoreYesterdayDate = function(variableName){
  var dates = new Date();
  var day = dates.getDate();
day=day-1;
  if (day < 10){
     day = day;
  }
  month = dates.getMonth() + 1;
  if (month < 10){
     month = month;
  }
  var year = dates.getFullYear();
  var prettyDay = month + '/' + day + '/' + year;
  storedVars[variableName]= prettyDay;
};

Selenium.prototype.doStoreCurrentDay = function(variableName){
  var dates = new Date();
  var day = dates.getDate();
  if (day < 10){
     day = day;
  }
  month = dates.getMonth() + 1;
  if (month < 10){
     month = month;
  }
  var year = dates.getFullYear();
  var prettyDay = day
  storedVars[variableName]= prettyDay;
};
Selenium.prototype.doRandom2digit = function(variableName){
  random = Math.floor(Math.random()*99);
  storedVars[variableName] = random;
};
Selenium.prototype.doRandom3digit = function(variableName){
  random = Math.floor(Math.random()*999);
  storedVars[variableName] = random;
};
Selenium.prototype.doRandom4digit = function(variableName){
  random = Math.floor(Math.random()*9999);
  storedVars[variableName] = random;
};
Selenium.prototype.doRandom5digit = function(variableName){
  random = Math.floor(Math.random()*99999);
  storedVars[variableName] = random;
};
Selenium.prototype.doStoreRandom = function(variableName){
  random = Math.floor(Math.random()*10000000);
  storedVars[variableName] = random;
};
Selenium.prototype.doStoreVariable1 = function(variableName, Value1){
  globalstoredVars[variableName] = Value1;
};

globalStoredVars = new Object();

Selenium.prototype.doStoreValueGlobal = function(target, varName) {
    if (!varName) {
        value = this.page().bodyText();
        globalStoredVars[target] = value;
        return;
    }
    var element = this.page().findElement(target);
    globalStoredVars[varName] = getInputValue(element);
};

Selenium.prototype.doStoreTextGlobal = function(target, varName) {
    var element = this.page().findElement(target);
    globalStoredVars[varName] = getText(element);
};

Selenium.prototype.doStoreAttributeGlobal = function(target, varName) {
    globalStoredVars[varName] = this.page().findAttribute(target);
};

Selenium.prototype.doStoreGlobal = function(value, varName) {
    globalStoredVars[varName] = value;
};

Selenium.prototype.replaceVariables = function(str) {
    var stringResult = str;
    var match = stringResult.match(/\$\{\w+\}/g);
    if (!match) {
        return stringResult;
    }

      for (var i = 0; match && i < match.length; i++) {
        var variable = match[i]; 
        var name = variable.substring(2, variable.length - 1); 
        var replacement = storedVars[name];
        if (replacement != undefined) {
            stringResult = stringResult.replace(variable, replacement);
        }
        var replacement = globalStoredVars[name];
        if (replacement != undefined) {
            stringResult = stringResult.replace(variable, replacement);
        }
    }
    return stringResult;
};



var fso=null;
var tf=null;

Selenium.prototype.doOpenLogFile = function(){    
	if (fso == null){
		this.index=1;
		fso = new ActiveXObject("Scripting.FileSystemObject");
		tf = fso.CreateTextFile("C:\\LogFile.xml", true); 
		tf.WriteLine("<TestSuite>");
		tf.WriteLine("<TestCase" + this.index +">");
	}
	else{
		tf.WriteLine("</TestCase" + this.index +">");
		this.index++;
		tf.WriteLine("<TestCase" + this.index +">");
	}
}
	
function xmlTestData() {
	this.testdata = null;
	this.index = null;
}

Selenium.prototype.doWriteLogFile = function(varItemName, varItemtoLog){  
	testdata = new xmlTestData();
	testdata.load(varItemName, varItemtoLog);
}


xmlTestData.prototype.load = function(varItemName, varItemtoLog1){
	tf.WriteLine("<" + varItemName + ">" + varItemtoLog1 + "</" + varItemName + ">"  ) ;
    tf.WriteBlankLines(1) ;
    Selenium.prototype.doEcho("----inside load function");
}

Selenium.prototype.doCloseLogFile = function(){    
	tf.WriteLine("</TestCase" + this.index +">");
	tf.WriteLine("</TestSuite>"  ) ;
}

Selenium.prototype.doSFChangeModule = function(modname, newmodname){
	
	var newmodpath1, newmodpath2, newmodpath;
	selenium.doEcho(modname);
	selenium.doEcho(newmodname);

	//newmodpath1 = 'link=' ;
	//newmodpath2 = newmodname;
	newmodpath = ("link=" + newmodname);
	
	if (modname==newmodname)
	selenium.doEcho("already same");
	else
	{selenium.doClick(newmodpath);
			  
	selenium.doWaitForPageToLoad(60000);
	}
		

}




Selenium.prototype.doForceLogoff = function(varXpath){
	
	var a;
	a = storedVars['varCntLogoff'];
	if (a==1)
	{
	selenium.doClick(varXpath);
			  
	selenium.doWaitForPageToLoad(60000);
	}
	else
		selenium.doEcho("alread loggedoff");
}

Selenium.prototype.doSubString = function(target, str) {
subStringResult="";


  if(target.indexOf(str)!=-1){
	

	storedVars['subStringResult']=true;
	return;
}
	
	
   else
	{
	

	storedVars['subStringResult']=false;
	return;
}
	
}


Selenium.prototype.doNotEqualTo = function(target, str) {
NotEqualToResult="";


  if(target == str){
	

	storedVars['NotEqualToResult']=false;
	return;
}
	
	
   else
	{
	

	storedVars['NotEqualToResult']=true;
	return;
}
	
}

Selenium.prototype.doForceLogoffPrsnlView = function(Ele_logout, a){
	
		if (a=="true")
	{
	selenium.doClick(Ele_logout);
			  
	selenium.doWaitForPageToLoad(60000);
	}
	else
		selenium.doEcho("alread loggedoff");
}





Selenium.prototype.doVerifyMilitaryUnGradStatus = function(){
       var a;
       a=storedVars['Status_MiliUnGrad'];
              if(a=="Enabled")
		{
          	 selenium.doClick("xpath=(//input[@value='Disable'])[3]");
       		} 
       
       }

Selenium.prototype.doMilitaryUnGradEnable = function(){
       var a;
       a=storedVars['Status_MiliUnGrad'];
              if(a=="Disabled")
		{
          	 selenium.doClick("//input[@value='Enable']");
       		} 
       
       }

Selenium.prototype.doVerifyMilitaryGradStatus = function(){
       var a;
       a=storedVars['Status_MiliGrad'];
              if(a=="Enabled")
		{
          	 selenium.doClick("xpath=(//input[@value='Disable'])[4]");
       		} 
       
       }

Selenium.prototype.doMilitaryGradEnable = function(){
       var a;
       a=storedVars['Status_MiliGrad'];
              if(a=="Disabled")
		{
          	 selenium.doClick("//input[@value='Enable']");
       		} 
       
       }
	   
Selenium.prototype.doVerifyGradStatus = function(){
       var a;
       a=storedVars['Current_Status'];
              if(a=="Enabled")
		{
          	 selenium.doClick("xpath=(//input[@value='Disable'])[2]");
       		} 
       
       }

Selenium.prototype.doGradEnable = function(){
       var a;
       a=storedVars['Current_Status'];
              if(a=="Disabled")
		{
          	 selenium.doClick("//input[@value='Enable']");
       		} 
       
       }

Selenium.prototype.doVerifyUnderGradStatus = function(){
       var a;
       a=storedVars['Current_Status'];
              if(a=="Enabled")
		{
          	 selenium.doClick("css=input.cmsbutton2.btn-active");
       		} 
       
       }

Selenium.prototype.doUnderGradEnable = function(){
       var a;
       a=storedVars['Current_Status'];
              if(a=="Disabled")
		{
          	 selenium.doClick("css=input.cmsbutton2.btn-active");
       		} 
       
       }
	   


Selenium.prototype.doCompareDates= function()
{
	 var a,b,c,d;	
	a = storedVars['CDLDate'];
        	b=storedVars['CDLDate1'];
	c=storedVars['DocumentName'];
	d=storedVars['Status'];	
	if(c=='CDL-Military' && d =='Approved')
  	{
   	if(a == b)
	{
		Selenium.prototype.doEcho("Fail:= Dates matched: CDL not generated");
	}
	else
	{
		Selenium.prototype.doEcho("Pass:= Dates not matched. CDL generated successfully");
	}
	}	
  else
       {
             Selenium.prototype.doEcho("Document is not CDL or is not in Approved Status");
       }
}


Selenium.prototype.doCountValues = function()
   {
		var cell;
                              var cell1
		cell = storedVars['cellcount'];
		cell1 = storedVars['cellcount1'];
   	 for(var i=0;i<=cell/6;i++)
	{
	if (Selenium.prototype.doVerifyText("\\html/body/div[3]/div/div[2]/div/div/div/form/div[4]/table/tbody[2]/tr["+i+"]/td[2]/span") =='CDL-Military');
	continue;
	}
	return i;
      if(cell =cell1)
          {
	Selenium.prototype.doEcho(" Existing CDL overwritten.");
          }
      else
           { 
	Selenium.prototype.doEcho("CDL for new term generated successfully");
	}


}
Selenium.prototype.doSubstrIndex = function(str, substr) {
    	var i, substrindx;
	i= str.indexOf(substr);
	storedVars['substrindx']=i;
        return;
}

Selenium.prototype.doGetsIF = function(str) {
    	var len, i,j, substr;
	i= str.indexOf("SIFID");
	j= str.indexOf("SIFVersionID");
	i=i+7;
	j=j-2;
	len=j-i;
	substr=str.substr(i,len);
	storedVars['SIFID']=substr;
	        return;
}
Selenium.prototype.doGetsIFVersionID = function(str) {
    	var len, i,j, substr;
	i= str.indexOf("SIFVersionID");
	j= str.indexOf("VendorDate");
	i=i+14;
	j=j-2;
	len=j-i;
	substr=str.substr(i,len);
	storedVars['SIFVersionID']=substr;
	        return;
}
Selenium.prototype.doGetLeadID=function(){

var wn=selenium.getAllWindowNames();
this.doEcho("window 1: "+wn[0]);
this.doEcho("window 2: "+wn[1]);

this.doEcho("array length: "+selenium.getAllWindowNames().length);
this.doEcho("Available window names: "+selenium.getAllWindowNames());
this.doEcho("Selecting window: "+storedVars['windowName']);


}