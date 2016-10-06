#create a new directory that will contain out generated apk
mkdir $HOME/buildApk/
#copy generated apk from build folder to the folder just created
cp -R ./ANNA/app/build/outputs/apk/* $HOME/buildApk/
#go to home and setup git
cd $HOME
git config --global user.email "aram.parsegyan@gmail.com"
git config --global user.name "Aaper" 
#clone the repository in the buildApk folder
git clone --quiet https://Aaper:$GITHUB_API_KEY@github.com/AnnaForAndroid/ANNA
#go into directory and copy data we're interested
cp -Rf $HOME/buildApk/* ./ANNA/
#add, commit and push files
git add -f .
git commit -m "[skip ci] Travis build $TRAVIS_BUILD_NUMBER pushed"
git push