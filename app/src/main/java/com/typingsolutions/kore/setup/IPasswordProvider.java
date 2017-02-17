package com.typingsolutions.kore.setup;

interface IPasswordProvider {
  CharSequence getPassword1();

  CharSequence getPassword2();

  void setPasswords(CharSequence pw1, CharSequence pw2);

  void cleanUp();
}
