import React, {Component} from 'react';
import {connect} from 'react-redux';
import RaisedButton from 'material-ui/RaisedButton';

import _ from "lodash";
import ShowFields from "./showFields";

import {translate} from '../common/common';
import Api from '../../api/api';
import jp from "jsonpath";
import UiButton from './components/UiButton';
import {fileUpload, getInitiatorPosition} from './utility/utility';
var specifications={};
try {
  var hash = window.location.hash.split("/");
  if(hash.length == 3 || (hash.length == 4 && hash.indexOf("update") > -1)) {
    specifications = require(`./specs/${hash[2]}/${hash[2]}`).default;
  } else {
    specifications = require(`./specs/${hash[2]}/master/${hash[3]}`).default;
  }
} catch(e) {

}
let reqRequired = [];
class Report extends Component {
  constructor(props) {
    super(props);
  }

  setLabelAndReturnRequired(configObject) {
    if(configObject && configObject.groups) {
      for(var i=0;configObject && i<configObject.groups.length; i++) {
        configObject.groups[i].label = translate(configObject.groups[i].label);
        for (var j = 0; j < configObject.groups[i].fields.length; j++) {
              configObject.groups[i].fields[j].label = translate(configObject.groups[i].fields[j].label);
              if (configObject.groups[i].fields[j].isRequired)
                  reqRequired.push(configObject.groups[i].fields[j].jsonPath);
        }

        if(configObject.groups[i].children && configObject.groups[i].children.length) {
          for(var k=0; k<configObject.groups[i].children.length; k++) {
            this.setLabelAndReturnRequired(configObject.groups[i].children[k]);
          }
        }
      }
    }
  }

  setDefaultValues (groups, dat) {
    for(var i=0; i<groups.length; i++) {
      for(var j=0; j<groups[i].fields.length; j++) {
        if(groups[i].fields[j].defaultValue) {
          _.set(dat, groups[i].fields[j].jsonPath, groups[i].fields[j].defaultValue);
        }

        if(groups[i].fields[j].children && groups[i].fields[j].children.length) {
          for(var k=0; k<groups[i].fields[j].children.length; k++) {
            this.setDefaultValues(groups[i].fields[j].children[k].groups);
          }
        }
      }
    }
  }

  initData() {
    let { setMetaData, setModuleName, setActionName, initForm, setMockData, setFormData } = this.props;
    let self = this;
    let hashLocation = window.location.hash;
    let obj = specifications[`${hashLocation.split("/")[2]}.${hashLocation.split("/")[1]}`];
    this.setLabelAndReturnRequired(obj);
    initForm(reqRequired, []);
    setMetaData(specifications);
    setMockData(JSON.parse(JSON.stringify(specifications)));
    setModuleName(hashLocation.split("/")[2]);
    setActionName(hashLocation.split("/")[1]);

    if(hashLocation.split("/").indexOf("update") > -1) {
      var url = specifications[`${hashLocation.split("/")[2]}.${hashLocation.split("/")[1]}`].searchUrl.split("?")[0];
      var id = this.props.match.params.id || this.props.match.params.master;
      var query = {
        [specifications[`${hashLocation.split("/")[2]}.${hashLocation.split("/")[1]}`].searchUrl.split("?")[1].split("=")[0]]: id
      };
      Api.commonApiPost(url, query, {}, false, specifications[`${hashLocation.split("/")[2]}.${hashLocation.split("/")[1]}`].useTimestamp).then(function(res){
        self.props.setFormData(res);
      }, function(err){

      })
    } else {
      var formData = {};
      if(obj && obj.groups && obj.groups.length) self.setDefaultValues(obj.groups, formData);
      setFormData(formData);
    }
  }

  componentDidMount() {
      this.initData();
  }

  autoComHandler = (autoObject, path) => {
    let self = this;
    var value = this.getVal(path);
    if(!value) return;
    var url = autoObject.autoCompleteUrl.split("?")[0];
    var hashLocation = window.location.hash;
    var query = {
        [autoObject.autoCompleteUrl.split("?")[1].split("=")[0]]: value
    };
    Api.commonApiPost(url, query, {}, false, specifications[`${hashLocation.split("/")[2]}.${hashLocation.split("/")[1]}`].useTimestamp).then(function(res){
        var formData = {...this.props.formData};
        for(var key in autoObject.autoFillFields) {
          _.set(formData, key, _.get(res, autoObject.autoFillFields[key]));
        }
        self.props.setFormData(formData);
    }, function(err){
      console.log(err);
    })
  }

  makeAjaxCall = (formData) => {
    let self = this;
    Api.commonApiPost(self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].url, "", formData, "", true).then(function(response){
      self.props.setLoadingStatus('hide');
      self.props.toggleSnackbarAndSetText(true, translate("wc.create.message.success"), true);
      self.initData();
    }, function(err) {
      self.props.setLoadingStatus('hide');
      self.props.toggleSnackbarAndSetText(true, err.message);
    })
  }

  //Needs to be changed later for more customfields
  checkCustomFields = (formData, cb) => {
    var self = this;
    if(self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].customFields && self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].customFields.initiatorPosition) {
      var jPath = self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].customFields.initiatorPosition;
      getInitiatorPosition(function(err, pos) {
        if(err) {
          self.toggleSnackbarAndSetText(true, err.message);
        } else {
          _.set(formData, jPath, pos);
          cb(formData);
        }
      })
    } else {
      cb(formData);
    }
  }

  create=(e) => {
    let self = this;
    e.preventDefault();
    self.props.setLoadingStatus('loading');
    var formData = Object.assign(this.props.formData);

    if(self.props.moduleName && self.props.actionName && self.props.metaData && self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].tenantIdRequired) {
      if(!formData[self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].objectName])
        formData[self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].objectName] = {};

      formData[self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].objectName]["tenantId"] = localStorage.getItem("tenantId") || "default";
    }

    //Check if documents, upload and get fileStoreId
    // if(formData[self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].objectName]["documents"] && formData[self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].objectName]["documents"].length) {
    //   let documents = [...formData[self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].objectName]["documents"]];
    //   let _docs = [];
    //   let counter = documents.length, breakOut = 0;
    //   for(let i=0; i<documents.length; i++) {
    //     fileUpload(documents[i], self.props.moduleName, function(err, res) {
    //       if(breakOut == 1) return;
    //       if(err) {
    //         breakOut = 1;
    //         self.props.setLoadingStatus('hide');
    //         self.props.toggleSnackbarAndSetText(true, err, false, true);
    //       } else {
    //         _docs.push({
    //           fileStoreId: res.files[0].fileStoreId
    //         })
    //         counter--;
    //         if(counter == 0 && breakOut == 0) {
    //           formData[self.props.metaData[`${self.props.moduleName}.${self.props.actionName}`].objectName]["documents"] = _docs;
    //           self.makeAjaxCall(formData);
    //         }
    //       }
    //     })
    //   }
    // } else {
      self.makeAjaxCall(formData);

  }

  getVal = (path) => {
    return _.get(this.props.formData, path) || "";
  }

  handleChange=(e, property, isRequired, pattern, requiredErrMsg="Required",patternErrMsg="Pattern Missmatch") => {
      let {getVal}=this;
      let {handleChange,mockData,setDropDownData}=this.props;
      let hashLocation = window.location.hash;
      let obj = specifications[`${hashLocation.split("/")[2]}.${hashLocation.split("/")[1]}`];
      // console.log(obj);
      let depedants=jp.query(obj,`$.groups..fields[?(@.jsonPath=="${property}")].depedants.*`);
      handleChange(e,property, isRequired, pattern, requiredErrMsg, patternErrMsg);

      _.forEach(depedants, function(value,key) {
            if (value.type=="dropDown") {
                let splitArray=value.pattern.split("?");
                let context="";
          			let id={};
          			// id[splitArray[1].split("&")[1].split("=")[0]]=e.target.value;
          			for (var j = 0; j < splitArray[0].split("/").length; j++) {
          				context+=splitArray[0].split("/")[j]+"/";
          			}

          			let queryStringObject=splitArray[1].split("|")[0].split("&");
          			for (var i = 0; i < queryStringObject.length; i++) {
          				if (i) {
                    if (queryStringObject[i].split("=")[1].search("{")>-1) {
                      if (queryStringObject[i].split("=")[1].split("{")[1].split("}")[0]==property) {
                        id[queryStringObject[i].split("=")[0]]=e.target.value || "";
                      } else {
                        id[queryStringObject[i].split("=")[0]]=getVal(queryStringObject[i].split("=")[1].split("{")[1].split("}")[0]);
                      }
                    } else {
                      id[queryStringObject[i].split("=")[0]]=queryStringObject[i].split("=")[1];
                    }
          				}
          			}

                Api.commonApiPost(context,id).then(function(response)
                {
                  let keys=jp.query(response,splitArray[1].split("|")[1]);
                  let values=jp.query(response,splitArray[1].split("|")[2]);
                  let dropDownData=[];
                  for (var k = 0; k < keys.length; k++) {
                      let obj={};
                      obj["key"]=keys[k];
                      obj["value"]=values[k];
                      dropDownData.push(obj);
                  }
                  setDropDownData(value.jsonPath,dropDownData);
                },function(err) {
                    console.log(err);
                });
                // console.log(id);
                // console.log(context);
            }

            else if (value.type=="textField") {
              let object={
                target:{
                  value:eval(eval(value.pattern))
                }
              }
              handleChange(object,value.jsonPath,value.isRequired,value.rg,value.requiredErrMsg,value.patternErrMsg);
            }
      });

  }

  incrementIndexValue = (group, jsonPath) => {
    let {formData} = this.props;
    var length = _.get(formData, jsonPath) ? _.get(formData, jsonPath).length : 0;
    var _group = JSON.stringify(group);
    var regexp = new RegExp(jsonPath + "\\[\\d{1}\\]", "g");
    _group = _group.replace(regexp, jsonPath + "[" + (length+1) + "]");
    return JSON.parse(_group);
  }

  getNewSpecs = (group, updatedSpecs, path) => {
    let {moduleName, actionName} = this.props;
    let groupsArray = _.get(updatedSpecs[moduleName + "." + actionName], path);
    groupsArray.push(group);
    _.set(updatedSpecs[moduleName + "." + actionName], path, groupsArray);
    return updatedSpecs;
  }

  getPath = (value) => {
    let {mockData, moduleName, actionName} = this.props;
    const getFromGroup = function(groups) {
      for(var i=0; i<groups.length; i++) {
        for(var j=0; j<groups[i].children.length; i++) {
          if(groups[i].children[j].jsonPath == value) {
            return "groups[" + i + "].children[" + j + "].groups";
          } else {
            return "groups[" + i + "].children[" + j + "][" + getFromGroup(groups[i].children[j].groups) + "]";
          }
        }
      }
    }

    return getFromGroup(mockData[moduleName + "." + actionName].groups);
  }

  addNewCard = (group, jsonPath) => {
    let self = this;
    group = JSON.parse(JSON.stringify(group));
    let {setMockData, mockData, metaData, moduleName, actionName} = this.props;
    //Increment the values of indexes
    var grp = _.get(metaData[moduleName + "." + actionName], self.getPath(jsonPath)+ '[0]');
    group = this.incrementIndexValue(grp, jsonPath);
    //Push to the path
    var updatedSpecs = this.getNewSpecs(group, JSON.parse(JSON.stringify(mockData)), self.getPath(jsonPath));
    //Create new mock data
    setMockData(updatedSpecs);
  }

  removeCard = (jsonPath, index) => {
    //Remove at that index and update upper array values
    let {mockData, setMockData, formData} = this.props;

  }

  render() {
    let {mockData, moduleName, actionName, formData, fieldErrors} = this.props;
    let {create, handleChange, getVal, addNewCard, removeCard, autoComHandler} = this;
    return (
      <div className="Report">
        <form onSubmit={(e) => {
          create(e)
        }}>
        {!_.isEmpty(mockData) && <ShowFields
                                    groups={mockData[`${moduleName}.${actionName}`].groups}
                                    noCols={mockData[`${moduleName}.${actionName}`].numCols}
                                    ui="google"
                                    handler={handleChange}
                                    getVal={getVal}
                                    fieldErrors={fieldErrors}
                                    useTimestamp={mockData[`${moduleName}.${actionName}`].useTimestamp || false}
                                    addNewCard={addNewCard}
                                    removeCard={removeCard}
                                    autoComHandler={autoComHandler}/>}
          <div style={{"textAlign": "center"}}>
            <br/>
            {actionName == "create" && <UiButton item={{"label": "Create", "uiType":"submit"}} ui="google"/>}
            {actionName == "update" && <UiButton item={{"label": "Update", "uiType":"submit"}} ui="google"/>}
            <br/>
          </div>
        </form>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  metaData:state.framework.metaData,
  mockData: state.framework.mockData,
  moduleName:state.framework.moduleName,
  actionName:state.framework.actionName,
  formData:state.frameworkForm.form,
  fieldErrors: state.frameworkForm.fieldErrors
});

const mapDispatchToProps = dispatch => ({
  initForm: (reqRequired, patRequired) => {
    dispatch({
      type: "RESET_STATE",
      validationData: {
        required: {
          current: [],
          required: reqRequired
        },
        pattern: {
          current: [],
          required: patRequired
        }
      }
    });
  },
  setMetaData: (metaData) => {
    dispatch({type:"SET_META_DATA", metaData})
  },
  setMockData: (mockData) => {
    dispatch({type: "SET_MOCK_DATA", mockData});
  },
  setFormData: (data) => {
    dispatch({type: "SET_FORM_DATA", data});
  },
  setModuleName: (moduleName) => {
    dispatch({type:"SET_MODULE_NAME", moduleName})
  },
  setActionName: (actionName) => {
    dispatch({type:"SET_ACTION_NAME", actionName})
  },
  handleChange: (e, property, isRequired, pattern, requiredErrMsg, patternErrMsg)=>{
    dispatch({type:"HANDLE_CHANGE_VERSION_TWO",property,value: e.target.value, isRequired, pattern, requiredErrMsg, patternErrMsg});
  },
  setLoadingStatus: (loadingStatus) => {
    dispatch({type: "SET_LOADING_STATUS", loadingStatus});
  },
  toggleSnackbarAndSetText: (snackbarState, toastMsg, isSuccess, isError) => {
    dispatch({type: "TOGGLE_SNACKBAR_AND_SET_TEXT", snackbarState, toastMsg, isSuccess, isError});
  },
  setDropDownData:(fieldName,dropDownData)=>{
    dispatch({type:"SET_DROPDWON_DATA",fieldName,dropDownData})
  }
});

export default connect(mapStateToProps, mapDispatchToProps)(Report);
