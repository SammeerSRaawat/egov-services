var dat = {
	"tl.create": {
		"numCols": 12/3,
		"url": "/tl-masters/category/v1/_create",
		"useTimestamp": true,
		"tenantIdRequired": true,
		"objectName": "categories",
		"groups": [
			{
				"label": "tl.create.groups.subcategorytype.title",
				"name": "createLicenseSubCategoryType",
				"fields": [

          {
            "name": "createCategory",
            "jsonPath": "categories[0].parentId",
            "label": "tl.create.groups.subcategorytype.category",
            "pattern": "",
            "type": "singleValueList",
            "url": "/tl-masters/category/v1/_search?tenantId=default&type=category|$..id|$..name",
            "isRequired": true,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": ""
          },
						{
							"name": "createName",
							"jsonPath": "categories[0].name",
							"label": "tl.create.groups.subcategorytype.name",
							"pattern": "^.[a-zA-Z. ]{2,49}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Enter Valid Name (Min:3, Max:50)",
							"maxLength": "50"
						},
						{
							"name": "createCode",
							"jsonPath": "categories[0].code",
							"label": "tl.create.groups.subcategorytype.code",
							"pattern": "^.[A-Za-z0-9]{0,19}$",
							"type": "text",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Enter Valid Code (Alpha-Numeric, Min:1, Max:20)",
							"maxLength": "20"
						},
						{
							"name": "createValidityYears",
							"jsonPath": "categories[0].validityYears",
							"label": "tl.create.groups.subcategorytype.validityYears",
							"pattern": "^([1-9]|10)$",
							"type": "number",
							"isRequired": true,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "Enter Valid Validity Year (Min: 1, Max:10)",
							"maxLength": "2"
						},
						{
							"name": "createActive",
							"jsonPath": "categories[0].active",
							"label": "tl.create.groups.subcategorytype.active",
							"pattern": "",
							"type": "checkbox",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "",
							"defaultValue":true
						},
						{
		          "name": "craeteType",
		          "jsonPath": "categories[0].type",
		          "label": "tl.craete.groups.subcategorytype.category",
		          "pattern": "",
		          "type": "text",
		          "url": "",
		          "isRequired": false,
		          "isDisabled": false,
		          "requiredErrMsg": "",
		          "patternErrMsg": "",
		          "defaultValue": "SUBCATEGORY",
		          "isHidden": true
		        }

				]
			},

			{
				"label": "tl.create.groups.subcategorytype.details",
				"name": "createDetails",
				"jsonPath": "categories[0].details",
				"multiple":true,
				"fields": [
					{
						"name": "createFeeType",
						"jsonPath": "categories[0].details[0].feeType",
						"label": "tl.create.groups.subcategorytype.categories.details.feeType",
						"pattern": "",
						"type": "singleValueList",
						"url": "",
						"isRequired": true,
						"isDisabled": false,
						"requiredErrMsg": "",
						"patternErrMsg": "",
						"defaultValue": [
          {
            "key": "LICENSE",
            "value": "LICENSE"
          },
          {
            "key": "MOTOR",
            "value": "MOTOR"
          },
          {
            "key": "WORKFORCE",
            "value": "WORKFORCE"
          }
            ]
					},
					{
            "name": "createRateType",
            "jsonPath": "categories[0].details[0].rateType",
            "label": "tl.create.groups.subcategorytype.categories.details.rateType",
            "pattern": "",
            "type": "singleValueList",
            "url": "",
            "isRequired": true,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": "",
						"defaultValue": [
				 {
					 "key": "FLAT_BY_RANGE",
					 "value": "FLAT BY RANGE"
				 },
				 {
					 "key": "FLAT_BY_PERCENTAGE",
					 "value": "FLAT BY PERCENTAGE"
				 },
				 {
					 "key": "UNIT_BY_RANGE",
					 "value": "UNIT BY RANGE"
				 }
					 ]
          },
					{
            "name": "createUomId",
            "jsonPath": "categories[0].details[0].uomId",
            "label": "tl.create.groups.subcategorytype.categories.details.uomId",
            "pattern": "",
            "type": "singleValueList",
            "url": "/tl-masters/uom/v1/_search?|$..id|$..name",
            "isRequired": true,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": ""
          },
					{
						"name": "createTenantID",
						"jsonPath": "categories[0].details[0].tenantId",
						"label": "tenantId",
						"pattern": "",
						"type": "text",
						"isRequired": true,
						"isDisabled": false,
						"requiredErrMsg": "",
						"patternErrMsg": "",
						"defaultValue": localStorage.getItem("tenantId"),
						"hide": "true"
					}
					]
				}

		]
	},


  "tl.search": {
    "numCols": 12/2,
    "url": "/tl-masters/category/v1/_search",
    "tenantIdRequired": true,
    "useTimestamp": true,
    "objectName": "categories",
    "groups": [
      {
        "label": "tl.search.groups.subcategorytype.title",
        "name": "searchCategoryType",
        "fields": [

            {
              "name": "searchCategory",
              "jsonPath": "categoryId",
              "label": "tl.search.groups.subcategorytype.category",
              "pattern": "",
              "type": "singleValueList",
              "url": "/tl-masters/category/v1/_search?tenantId=default|$..id|$..name",
              "isRequired": false,
              "isDisabled": false,
              "requiredErrMsg": "",
              "patternErrMsg": "",
							"depedants": [{
	              "jsonPath": "ids",
	              "type": "dropDown",
	              "pattern": "/tl-masters/category/v1/_search?tenantId=default&type=SUBCATEGORY&categoryId={categoryId}|$.categories.*.id|$.categories.*.name"
	            }]
            },
            {
              "name": "searchSubCategory",
              "jsonPath": "ids",
              "label": "tl.search.groups.subcategorytype.subcategory",
              "pattern": "",
              "type": "singleValueList",
              "url": "",
              "isRequired": false,
              "isDisabled": false,
              "requiredErrMsg": "",
              "patternErrMsg": ""
            },
						{
							"name": "searchType",
							"jsonPath": "type",
							"label": "tl.search.groups.subcategorytype.category",
							"pattern": "",
							"type": "text",
							"url": "",
							"isRequired": false,
							"isDisabled": false,
							"requiredErrMsg": "",
							"patternErrMsg": "",
							"defaultValue": "SUBCATEGORY",
							"hide": true
						}
        ]
      }
    ],
    "result": {
      "header": [{label: "tl.create.groups.subcategorytype.code"},{label: "tl.create.groups.subcategorytype.name"}, {label: "tl.create.groups.subcategorytype.category"}, {label: "tl.create.groups.subcategorytype.active"}, {label: "tl.create.groups.subcategorytype.categories.details.feeType"}, {label: "tl.create.groups.subcategorytype.categories.details.rateType"}, {label: "tl.create.groups.subcategorytype.categories.details.uomId"}],
      "values": ["code", "name","parentName", "active", "details[0].feeType", "details[0].rateType", "details[0].uomName"],
      "resultPath": "categories",
      "rowClickUrlUpdate": "/non-framework/tl/transaction/UpdateSubCategory/{id}",
      "rowClickUrlView": "/view/tl/CreateLicenseSubCategory/{id}"
      }
  },

  "tl.view": {
    "numCols": 12/3,
    "url": "/tl-masters/category/v1/_search?ids={id}",
    "tenantIdRequired": true,
    "useTimestamp": true,
    "objectName": "categories[0]",
    "groups": [
			{
				"label": "tl.view.groups.categorytype.title",
				"name": "viewCategoryType",
				"fields": [

					{
            "name": "viewCategory",
            "jsonPath": "categories[0].parentName",
            "label": "tl.view.groups.subcategorytype.category",
            "pattern": "",
            "type": "text",
            "url": "/tl-masters/category/v1/_view?|$..id|$..name",
            "isRequired": true,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": ""
          },
					{
            "name": "viewName",
            "jsonPath": "categories[0].name",
            "label": "tl.view.groups.subcategorytype.name",
            "pattern": "",
            "type": "text",
            "isRequired": true,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": ""
          },
					{
            "name": "viewCode",
            "jsonPath": "categories[0].code",
            "label": "tl.view.groups.subcategorytype.code",
            "pattern": "",
            "type": "text",
            "isRequired": false,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": ""
          },
					{
						"name": "viewValidityYears",
						"jsonPath": "categories[0].validityYears",
						"label": "tl.view.groups.subcategorytype.validityYears",
						"pattern": "^([1-9]|10)$",
						"type": "number",
						"isRequired": true,
						"isDisabled": false,
						"requiredErrMsg": "",
						"patternErrMsg": "Enter Valid Validity Year (Min: 1, Max:10)"
					},
					{
						"name": "viewActive",
						"jsonPath": "categories[0].active",
						"label": "tl.view.groups.subcategorytype.active",
						"pattern": "",
						"type": "text",
						"isRequired": false,
						"isDisabled": false,
						"requiredErrMsg": "",
						"patternErrMsg": "",
						"defaultValue":true
					}

				]
			},
			{
				"label": "tl.create.groups.subcategorytype.details",
				"name": "createDetails",
				"jsonPath": "categories[0].details",
				"multiple": true,
				"fields": [
					{
						"name": "createFeeType",
						"jsonPath": "categories[0].details[0].feeType",
						"label": "tl.create.groups.subcategorytype.categories.details.feeType",
						"pattern": "",
						"type": "text",
						"url": "",
						"isRequired": true,
						"isDisabled": false,
						"requiredErrMsg": "",
						"patternErrMsg": "",
						"defaultValue": [
          {
            "key": "LICENSE",
            "value": "LICENSE"
          },
          {
            "key": "MOTOR",
            "value": "MOTOR"
          },
          {
            "key": "WORKFORCE",
            "value": "WORKFORCE"
          }
            ]
					},
					{
            "name": "createRateType",
            "jsonPath": "categories[0].details[0].rateType",
            "label": "tl.create.groups.subcategorytype.categories.details.rateType",
            "pattern": "",
            "type": "singleValueList",
            "url": "",
            "isRequired": true,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": "",
						"defaultValue": [
				 {
					 "key": "FLAT_BY_RANGE",
					 "value": "FLAT BY RANGE"
				 },
				 {
					 "key": "FLAT_BY_PERCENTAGE",
					 "value": "FLAT BY PERCENTAGE"
				 },
				 {
					 "key": "UNIT_BY_RANGE",
					 "value": "UNIT BY RANGE"
				 }
					 ]
          },
					{
            "name": "createUomId",
            "jsonPath": "categories[0].details[0].uomId",
            "label": "tl.create.groups.subcategorytype.categories.details.uomId",
            "pattern": "",
            "type": "singleValueList",
            "url": "/tl-masters/uom/v1/_search?|$..id|$..name",
            "isRequired": true,
            "isDisabled": false,
            "requiredErrMsg": "",
            "patternErrMsg": ""
          }


					]
				}
    ]
  },

	"tl.update": {
		"numCols": 12/3,
		"searchUrl": "/tl-masters/category/v1/_search?ids={id}",
		"url": "/tl-masters/category/v1/_update",
		"isResponseArray":true,
		"tenantIdRequired": true,
		"useTimestamp": true,
		"objectName": "categories[0]",
		"groups": [
			{
				"label": "tl.update.groups.subcategorytype.title",
				"name": "createCategoryType",
				"jsonPath": "categories",
				"fields": [

					{
						"name": "updateCategory",
						"jsonPath": "categories[0].parentName",
						"label": "tl.update.groups.subcategorytype.category",
						"pattern": "",
						"type": "text",
						"url": "",
						"isRequired": true,
						"isDisabled": true,
						"requiredErrMsg": "",
						"patternErrMsg": ""
					},
					{
	          "name": "updateName",
	          "jsonPath": "categories[0].name",
	          "label": "tl.update.groups.subcategorytype.name",
	          "pattern": "^.[a-zA-Z. ]{2,49}$",
	          "type": "text",
	          "isRequired": true,
	          "isDisabled": false,
	          "requiredErrMsg": "",
	          "patternErrMsg": "Enter Valid Name (Min:3, Max:50)",
						"maxLength": "50"
	        },
	        {
	          "name": "updateCode",
	          "jsonPath": "categories[0].code",
	          "label": "tl.update.groups.subcategorytype.code",
	          "pattern": "^.[A-Za-z0-9]{14,14}$",
	          "type": "text",
	          "isRequired": true,
	          "isDisabled": true,
	          "requiredErrMsg": "",
	          "patternErrMsg": "Enter 15 digit Alpha/Numeric Code"
	        },
	        {
	          "name": "updateValidityYears",
	          "jsonPath": "categories[0].validityYears",
	          "label": "tl.update.groups.subcategorytype.validityYears",
	          "pattern": "^([1-9]|10)$",
	          "type": "number",
	          "isRequired": true,
	          "isDisabled": false,
	          "requiredErrMsg": "",
	          "patternErrMsg": "Enter Valid Validity Year (Min: 1, Max:10)"
	        },
	        {
		          "name": "updateActive",
		          "jsonPath": "categories[0].active",
		          "label": "tl.update.groups.subcategorytype.active",
		          "pattern": "",
		          "type": "checkbox",
		          "isRequired": false,
		          "isDisabled": false,
		          "requiredErrMsg": "",
		          "patternErrMsg": "",
		          "defaultValue":true
		        },

	        // {
	        //   "name": "updateType",
	        //   "jsonPath": "categories[0].type",
	        //   "label": "typeParameter",
	        //   "pattern": "",
	        //   "type": "text",
	        //   "url": "",
	        //   "isRequired": false,
	        //   "isDisabled": false,
	        //   "requiredErrMsg": "",
	        //   "patternErrMsg": "",
	        //   "defaultValue": "SUBCATEGORY",
	        //   "isHidden": false
	        // }

				]
			},
			{
				"label": "tl.update.groups.subcategorytype.details",
				"name": "createCategoryType2",
				"jsonPath": "categories",
				"multiple": true,
				"fields": [
					{
	          "name": "updateFeeType",
	          "jsonPath": "categories[0].details[0].feeType",
	          "label": "tl.update.groups.subcategorytype.categories.details.feeType",
	          "pattern": "",
	          "type": "singleValueList",
	          "url": "",
	          "isRequired": true,
	          "isDisabled": false,
	          "requiredErrMsg": "",
	          "patternErrMsg": "",
	          "defaultValue": [
	        {
	          "key": "LICENSE",
	          "value": "LICENSE"
	        },
	        {
	          "key": "MOTOR",
	          "value": "MOTOR"
	        },
	        {
	          "key": "WORKFORCE",
	          "value": "WORKFORCE"
	        }
	          ]
	        },
					{
	          "name": "updateRateType",
	          "jsonPath": "categories[0].details[0].rateType",
	          "label": "tl.create.groups.subcategorytype.categories.details.rateType",
	          "pattern": "",
	          "type": "singleValueList",
	          "url": "",
	          "isRequired": true,
	          "isDisabled": false,
	          "requiredErrMsg": "",
	          "patternErrMsg": "",
	          "defaultValue": [
	       {
	         "key": "FLAT_BY_RANGE",
	         "value": "FLAT BY RANGE"
	       },
	       {
	         "key": "FLAT_BY_PERCENTAGE",
	         "value": "FLAT BY PERCENTAGE"
	       },
	       {
	         "key": "UNIT_BY_RANGE",
	         "value": "UNIT BY RANGE"
	       }
	         ]
	        },
					{
	          "name": "createUomId",
	          "jsonPath": "categories[0].details[0].uomId",
	          "label": "tl.create.groups.subcategorytype.categories.details.uomId",
	          "pattern": "",
	          "type": "singleValueList",
	          "url": "/tl-masters/uom/v1/_search?|$..id|$..name",
	          "isRequired": true,
	          "isDisabled": false,
	          "requiredErrMsg": "",
	          "patternErrMsg": ""
	        },
					{
	          "name": "createTenantID",
	          "jsonPath": "categories[0].details[0].tenantId",
	          "label": "tenantId",
	          "pattern": "",
	          "type": "text",
	          "isRequired": true,
	          "isDisabled": false,
	          "requiredErrMsg": "",
	          "patternErrMsg": "",
	          "defaultValue": localStorage.getItem("tenantId"),
	          "hide": "true"
	        }
				]
			}
		]
	}

}

export default dat;
