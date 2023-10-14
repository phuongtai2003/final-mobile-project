const gravatar = require('gravatar');
require('dotenv').config();
const jwt = require('jsonwebtoken');
const { Users } = require('../models');

const {TOKEN_TIME, TOKEN_SECRET_KEY} = process.env;
const avatarSize = process.env.avatarSize;
const userMail = process.env.userMail;
const passMail = process.env.passMail;
const nodemailer = require('nodemailer');

let transporter = nodemailer.createTransport({
    service: 'hotmail',
    auth: {
        user: userMail,
        pass: passMail
    }
});

const sendEmail = (email, password, res) => {
    let mailOptions = {
        from: userMail,
        to: email,
        subject: 'Password recovery',
        text: `Your password is: ${password}`
    };
    transporter.sendMail(mailOptions, (error, info) => {
        if (error) {
            res.status(500).json({ error: error.message });
        } else {
            res.status(200).json({ message: "Email sent" });
        }
    });
    return;
}

const createUser = async (req, res) => {
    let { email, password, username, almaMater, profileImage } = req.body;
    try {
        email = email.toLowerCase();
        if (!profileImage) {
            profileImage = gravatar.url(email, { s: avatarSize, r: 'x', d: 'retro', protocol: 'https' });
        }
        const user = await Users.create({ email, password, username, almaMater, profileImage });
        user.password = undefined;
        res.status(200).json({ user });
    } catch (error) {
        res.status(500).json({ error });
    }
};

const login = async (req, res) => {
    const { username, password } = req.body;
    try {
        const user = await Users.findOne({ username, password }).select('-password');
        if (!user) {
            res.status(401).json({ error: 'username or password is incorrect' });
            return;
        }
        const token = jwt.sign({ data: user }, TOKEN_SECRET_KEY, { expiresIn: TOKEN_TIME });
        res.status(200).json({ user , token});
    } catch (error) {
        res.status(500).json({ error });
    }
}

const updatePremium = async (req, res) => {
    const id = req.params.id || req.query.id;
    try {
        const user = await Users.findById(id).select('-password');
        if (!user) {
            res.status(404).json({ error: 'User not found' });
            return;
        }
        user.isPremiumAccount = true;
        await user.save();
        res.status(200).json({ user });
    } catch (error) {
        res.status(500).json({ error });
    }
}

const changePassword = async (req, res) => {
    const id = req.params.id || req.query.id;
    const { password, newPassword } = req.body;
    try {
        const user = await Users.findOne({ _id: id, password});
        if (!user) {
            res.status(404).json({ error: 'Invalid password' });
            return;
        }
        user.password = newPassword;
        await user.save();
        res.status(200).json({ error: "Change password successfully" });
    } catch (error) {
        res.status(500).json({ error });
    }
}

const updateUser = async (req, res) => {
    const id = req.params.id || req.query.id;
    const { almaMater, email } = req.body;
    try {
        const user = await Users.findByIdAndUpdate(id, { almaMater, email });
        if (!user) {
            res.status(404).json({ error: 'User not found' });
            return;
        }
        res.status(200).json({ message: "Update user successfully" });
    } catch (error) {
        res.status(500).json({ error });
    }
}

const uploadImage = async (req, res) => {
    const { file } = req;
    const urlImg = file?.path;
    const id = req.params.id || req.query.id;
    try {
        const user = await Users.findByIdAndUpdate(id, { profileImage: urlImg });
        if (!user) {
            res.status(404).json({ error: 'User not found' });
            return;
        }
        res.status(200).json({ message: "Change profile image successfully" });
    } catch (error) {
        res.status(500).json({ error });
    }
}

const passwordRecovery = async (req, res) => {
    const {email} = req.body;
    try {
        const user = await Users.findOne({email});
        sendEmail(email, user.password, res);
    } catch (error) {
        res.status(500).json({ error });
    }
}

const getAllUsers = async (req, res) => {
    try {
        const users = await Users.find().select("-password");
        if(users.length === 0){
            res.status(404).send({error: "list users are empty"});
            return;
        }
        res.status(200).json({users});
    } catch (error) {
        res.status(500).json({ error });
    }
}

const getUserById = async (req, res) =>{
    const id = req.params.id || req.query.id;
    try {
        const user = await Users.findById(id).select("-password");
        if(!user){
            res.status(404).send({error: "user not found"});
            return;
        }
        res.status(200).json({user});
    } catch (error) {
        res.status(500).json({ error });
    }
}

module.exports = {
    createUser,
    login,
    updatePremium,
    changePassword,
    updateUser,
    uploadImage,
    passwordRecovery,
    getAllUsers,
    getUserById
};