const express = require('express');
const foldersRouter = express.Router();

const { Folder } = require('../models');
const { authentication } = require('../middlewares/authentication/authenticate');
const {isCreated, isExistId, validateInput, isExistEmail, isCreatedUsername} = require('../middlewares/validation/validation');


// get all folders (tested)
foldersRouter.get("/", );
// get folder by id (tested)
foldersRouter.get("/:id", );
// create topic in folder (tested)
foldersRouter.post("/:id/topics", );
// add topic to folder (tested)
foldersRouter.put("/:id", );
// edit topic in folder (tested)
foldersRouter.put("/:id", );
// delete topic from folder (tested)
foldersRouter.delete("/:id", );