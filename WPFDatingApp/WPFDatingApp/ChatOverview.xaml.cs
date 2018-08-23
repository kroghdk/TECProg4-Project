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
using ChatBubbles;
using WPFDatingApp.ApiClasses;
using WPFDatingApp.Helpers;
using Page = System.Windows.Controls.Page;

namespace WPFDatingApp
{
    /// <summary>
    /// Interaction logic for ChatOverview.xaml
    /// </summary>
    public partial class ChatOverview : Page
    {
        public _mainFrame MainFrameRef { get; set; }

        public ChatOverview(_mainFrame mainFrame)
        {
            MainFrameRef = mainFrame;

            InitializeComponent();
            GetChats();
        }

        private void GetChats()
        {
            var response = HttpClientHelper.Get<JsonLogin>("GetConnections");

            if (response == null)
            {
                return;
            }

            var chats = response.Page.Rows.Row;

            foreach (var chat in chats)
            {
                var groupBox = new GroupBox
                {
                    Content = new Grid()
                    {
                        ColumnDefinitions = {new ColumnDefinition() {Width = new GridLength(0, GridUnitType.Star)}},
                        Children = { new Label(){Content = $"{chat.FirstName} {chat.LastName} - {chat.Age}", Width = 100, Visibility = Visibility.Visible}}
                    }
                };
                Grid.SetColumn(groupBox, 0);

                var button = new Button()
                {
                    Content = $"{chat.FirstName} {chat.LastName} - {chat.Age}",
                    Tag = chat.UserId
                };

                button.Click += ButtonOnClick;
                
                sp.Children.Add(button);
            }
        }

        private void ButtonOnClick(object sender, RoutedEventArgs e)
        {
            var userId = ((Button)sender).Tag.ToString();

            //ChatFrame.Content = new ChatPage(userid: userId);
            (App.Current.MainWindow as _mainFrame).MainFrame.Content = new ChatPage(userId, MainFrameRef);
        }
    }
}
