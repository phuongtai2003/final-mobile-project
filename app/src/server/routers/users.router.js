const express = require('express');
const usersRouter = express.Router();
require('dotenv').config();

const {Users} = require('../models');
const {uploadImg} = require('../middlewares/upload/uploadImage');
const {isCreated, isExistId, validateInput, isExistEmail, isCreatedUsername} = require('../middlewares/validation/validation');
const {authentication} = require('../middlewares/authentication/authenticate');
const { createUser,
        login,
        updatePremium,
        changePassword,
        updateUser,
        uploadImage,
        passwordRecovery,
        getAllUsers,
        getUserById } = require('../controllers/users.controller');

// create Account (tested)
usersRouter.post('/register', validateInput(['email', 'password', 'username', 'almaMater']), isCreated(Users), isCreatedUsername(Users), createUser);
// login (tested)
usersRouter.post('/login', validateInput(['email', 'password']), login);
// password recovery (tested)
usersRouter.post('/recover-password', authentication, validateInput(['email']), isExistEmail(Users), passwordRecovery);
// update to premium (tested)
usersRouter.put('/profiles/update-premium/:id', authentication, isExistId(Users), updatePremium);
// change password (tested)
usersRouter.put('/profiles/password/:id', authentication, validateInput(['password', 'newPassword']), isExistId(Users), changePassword);
// update user (tested)
usersRouter.put('/profiles/:id', authentication, isExistId(Users), updateUser);
// upload profile image (tested)
usersRouter.put('/profiles/change-profile-image/:id', authentication, isExistId(Users), uploadImg.single('profile-image'), uploadImage);
// get all users (tested)
usersRouter.get("/", getAllUsers);
// get user by id (tested)
usersRouter.get("/:id", isExistId(Users), getUserById);

module.exports = usersRouter;