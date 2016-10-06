#create a new directory that will contain out generated apk
mkdir $HOME/buildApk/
mkdir $HOME/repo/ 
#copy generated apk from build folder to the folder just created
cp -R ./ANNA/app/build/outputs/apk/app-debug.apk $HOME/buildApk/
#go to home and setup git
cd $HOME/repo
git config --global user.email "aram.parsegyan@gmail.com"
git config --global user.name "Aaper" 
#clone the repository in the buildApk folder
git clone --quiet https://Aaper:$GITHUB_API_KEY@github.com/AnnaForAndroid/ANNA
#go into directory and copy data we're interested
cd master  cp -Rf $HOME/buildApk/* .
#add, commit and push files
git add -f .
git remote rm origin
git remote add origin https://Aaper:$GITHUB_API_KEY@github.com/AnnaForAndroid/ANNA.git
git add -f .
git commit -m "[skip ci] Travis build $TRAVIS_BUILD_NUMBER pushed"
git push -fq origin master > /dev/null
echo -e "Donen"
