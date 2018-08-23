using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using WPFDatingApp.Login;

namespace WPFDatingApp
{
    /// <summary>
    /// Interaction logic for _mainFrame.xaml
    /// </summary>
    public partial class _mainFrame : Window
    {
        public _mainFrame()
        {
            InitializeComponent();
            //MainFrame.NavigationService.Navigate(new Swiper());
        }

        private void Search_Selected(object sender, RoutedEventArgs e)
        {
            MainFrame.Content = new Search();
            //Searchframe.Navigate(new Search());
        }

        private void Swiper_Selected(object sender, RoutedEventArgs e)
        {
            MainFrame.Content = new Swiper();
            //MainFrame.Navigate(new Swiper());
        }

        private void Logout_OnSelected_Selected(object sender, RoutedEventArgs e)
        {
            Properties.Settings.Default.SessionKey = string.Empty;

            var loginWindow = new LoginWindow();
            App.Current.MainWindow = loginWindow;
            this.Visibility = Visibility.Hidden;
            loginWindow.Show();
            return;
        }

        protected override void OnClosed(EventArgs e)
        {
            base.OnClosed(e);

            Application.Current.Shutdown();
        }

        private void Chats_OnSelected_Selected(object sender, RoutedEventArgs e)
        {
            MainFrame.Content = new ChatOverview(this);
        }
    }
}
