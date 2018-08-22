using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;
using System.Xml;
using System.Xml.Serialization;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using WPFDatingApp.ApiClasses;
using WPFDatingApp.Helpers;
using WPFDatingApp.Properties;
using Visibility = System.Windows.Visibility;

namespace WPFDatingApp.Login
{
    /// <summary>
    /// Interaction logic for LoginWindow.xaml
    /// </summary>
    public partial class LoginWindow : Window
    {
        protected override void OnClosed(EventArgs e)
        {
            base.OnClosed(e);

            Application.Current.Shutdown();
        }

        public LoginWindow()
        {
            var jsonLogin = HttpClientHelper.Get<JsonLogin>("ValidateSession");

            if (jsonLogin.Page?.Error == null)
            {
                // Switch window
                var mainWindow = new _mainFrame();
                App.Current.MainWindow = mainWindow;
                //this.Close();
                this.Visibility = Visibility.Hidden;
                mainWindow.Show();
                return;
            }


            InitializeComponent();
        }

        private void tbxUsername_TextChanged(object sender, TextChangedEventArgs e)
        {
            // If textbox has value set label to visable=false
            var textBox = sender as TextBox;

            lblUsername.Visibility = !string.IsNullOrWhiteSpace(textBox?.Text) ? Visibility.Hidden : Visibility.Visible;
        }

        private void pwdBox_PasswordChanged(object sender, RoutedEventArgs e)
        {
            var passwordBox = sender as PasswordBox;

            lblPassword.Visibility = !string.IsNullOrWhiteSpace(passwordBox?.Password) ? Visibility.Hidden : Visibility.Visible;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            // Login
            var username = tbxUsername.Text;
            var password = pwdBox.Password;

            if (string.IsNullOrWhiteSpace(username) || string.IsNullOrWhiteSpace(password))
            {
                // return error message
                LoginFailedSetStyle("Username & password must be filled");
                return;
            }

            var httpClient = new HttpClient
            {
                BaseAddress = new Uri(HttpClientHelper.ApiUrl + "Login"),
                DefaultRequestHeaders = {Accept = { new MediaTypeWithQualityHeaderValue("application/xml") } }
            };

            // Post login info
            var response = httpClient.GetStringAsync(string.Format($"?USERNAME={username}&PASSWORD={password}")).Result;

            if (response != null)
            {
                var doc = new XmlDocument();
                doc.LoadXml(response);
                string jsonText = JsonConvert.SerializeXmlNode(doc);
                var jsonLogin = JsonConvert.DeserializeObject<JsonLogin>(jsonText);

                if (string.IsNullOrEmpty(jsonLogin.Page?.Rows?.Row[0].SessionKey))
                {
                    // Error
                    LoginFailedSetStyle(jsonLogin.Page.Error.ErrorMessage);
                    return;
                }

                // Set session Key and user id in settings
                Settings.Default.SessionKey = jsonLogin.Page.Rows.Row[0].SessionKey;
                Settings.Default.Save();


                // Switch window
                var mainWindow = new _mainFrame();
                App.Current.MainWindow = mainWindow;
                //this.Close();
                this.Visibility = Visibility.Hidden;
                mainWindow.Show();
            }
        }

        private void LoginFailedSetStyle(string errorMessage)
        {
            tbxUsername.BorderBrush = Brushes.Red;
            pwdBox.BorderBrush = Brushes.Red;
            lblLoginFailed.Visibility = Visibility.Visible;
            lblLoginFailed.Content = errorMessage;
        }
    }
}
